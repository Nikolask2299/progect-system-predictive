const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://api-gateway.pdm.local";

/**
 * Типизированные ошибки HTTP-клиента.
 */
export class ApiError extends Error {
  status: number;
  path: string;

  constructor(status: number, message: string, path: string) {
    super(`${message} (${path}, ${status})`);
    this.name = "ApiError";
    this.status = status;
    this.path = path;
  }
}

/**
 * Базовая обёртка над fetch с централизованной обработкой ошибок.
 * Обрабатывает:
 *  - 404 → ApiError с кодом 404
 *  - 409 → ApiError с кодом 409 (недопустимый переход)
 *  - Network error → ApiError с message "Network error"
 */
async function request<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const url = `${API_BASE_URL}${path}`;

  const config: RequestInit = {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(url, config);

    if (!response.ok) {
      let detail = response.statusText;
      try {
        const body = await response.json();
        detail = body.message || body.error || detail;
      } catch {
        /* ignore JSON parse errors */
      }

      if (response.status === 404) {
        throw new ApiError(404, "Ресурс не найден", path);
      }
      if (response.status === 409) {
        throw new ApiError(409, "Недопустимый переход состояния", path);
      }
      throw new ApiError(response.status, detail, path);
    }

    // 204 No Content — вернуть null
    if (response.status === 204) {
      return null as T;
    }

    return response.json();
  } catch (error) {
    if (error instanceof ApiError) throw error;
    throw new ApiError(0, "Network error", path);
  }
}

export { API_BASE_URL };
export default request;
