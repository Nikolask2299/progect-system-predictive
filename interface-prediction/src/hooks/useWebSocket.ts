import { useEffect, useRef, useCallback, useState } from "react";
import type { AlertCreatedEvent } from "../api/types";

const DEFAULT_WS_URL =
  import.meta.env.VITE_WS_URL || "ws://localhost:8080";

/**
 * Хук для подписки на WebSocket-события новых алертов.
 * Реализует:
 *  - Подключение к ws://.../ws/alerts
 *  - Парсинг JSON в AlertCreatedEvent
 *  - Автоматическое закрытие при размонтировании
 *  - Переподключение с экспоненциальной паузой (1с → 2с → 4с → ... → max 30с)
 *
 * @param onEvent — callback, вызываемый при получении события
 */
export function useAlertEvents(
  onEvent: (event: AlertCreatedEvent) => void
): { isConnected: boolean; lastError: string | null } {
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectDelayRef = useRef(1000);
  const maxReconnectDelay = 30_000;
  const [isConnected, setIsConnected] = useState(false);
  const [lastError, setLastError] = useState<string | null>(null);
  const onEventRef = useRef(onEvent);
  onEventRef.current = onEvent;

  const connect = useCallback(() => {
    const wsUrl = `${DEFAULT_WS_URL}/ws/alerts`;

    try {
      const ws = new WebSocket(wsUrl);
      wsRef.current = ws;

      ws.onopen = () => {
        setIsConnected(true);
        setLastError(null);
        // Сбросить задержку переподключения
        reconnectDelayRef.current = 1000;
      };

      ws.onmessage = (event) => {
        try {
          const data: AlertCreatedEvent = JSON.parse(event.data);
          onEventRef.current(data);
        } catch {
          setLastError("Ошибка парсинга WebSocket-сообщения");
        }
      };

      ws.onerror = () => {
        setLastError("Ошибка WebSocket-соединения");
      };

      ws.onclose = (event) => {
        setIsConnected(false);
        // Не переподключаться, если закрытие было по инициативе клиента
        if (event.wasClean === false && wsRef.current === ws) {
          scheduleReconnect();
        }
      };
    } catch (err) {
      setLastError(
        err instanceof Error ? err.message : "Неизвестная ошибка подключения"
      );
      scheduleReconnect();
    }
  }, []);

  const scheduleReconnect = useCallback(() => {
    const delay = reconnectDelayRef.current;
    reconnectDelayRef.current = Math.min(
      reconnectDelayRef.current * 2,
      maxReconnectDelay
    );
    setTimeout(connect, delay);
  }, [connect]);

  useEffect(() => {
    connect();

    return () => {
      // Обязательное закрытие WebSocket при размонтировании (защита от утечек)
      if (wsRef.current) {
        wsRef.current.close();
        wsRef.current = null;
      }
      // Очистить таймер переподключения
      reconnectDelayRef.current = 1000;
    };
  }, [connect]);

  return { isConnected, lastError };
}
