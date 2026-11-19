import React, { useState, useCallback, useRef } from "react";

/**
 * Результат работы хука useAsync.
 */
export interface AsyncState<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
  refetch: () => void;
}

/**
 * Универсальный хук для управления асинхронными операциями.
 * Возвращает { data, loading, error, refetch }.
 * Автоматически запускает promiseFn при монтировании.
 *
 * @param promiseFn — функция, возвращающая промис с данными
 * @param deps — зависимости для автоматического перезапуска (опционально)
 */
export function useAsync<T>(
  promiseFn: () => Promise<T>,
  deps: unknown[] = []
): AsyncState<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  // useRef для хранения актуальной функции, чтобы избежать утечек
  const promiseFnRef = useRef(promiseFn);
  promiseFnRef.current = promiseFn;

  const refetch = useCallback(() => {
    setLoading(true);
    promiseFnRef.current().then(
      (result) => {
        setData(result);
        setLoading(false);
        setError(null);
      },
      (err) => {
        setError(err instanceof Error ? err : new Error(String(err)));
        setLoading(false);
      }
    );
  }, []);

  // Автоматический запуск при монтировании или изменении зависимостей
  React.useEffect(() => {
    let cancelled = false;
    setLoading(true);

    promiseFnRef.current().then(
      (result) => {
        if (!cancelled) {
          setData(result);
          setLoading(false);
          setError(null);
        }
      },
      (err) => {
        if (!cancelled) {
          setError(err instanceof Error ? err : new Error(String(err)));
          setLoading(false);
        }
      }
    );

    return () => {
      cancelled = true;
    };
  }, deps);

  return { data, loading, error, refetch };
}
