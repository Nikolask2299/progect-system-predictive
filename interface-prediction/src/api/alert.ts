import request from "./client";
import type {
  AlertDto,
  AlertDetailsDto,
  AlertFilters,
  Page,
} from "./types";

/**
 * GET /alerts?page=&size=&status=&deviceId=&severity=&fromDate=&toDate=&sort=
 * Список алертов с фильтрами и пагинацией.
 */
export function fetchAlerts(
  filters: AlertFilters = {}
): Promise<Page<AlertDto>> {
  const params = new URLSearchParams();
  if (filters.status) params.set("status", filters.status);
  if (filters.deviceId) params.set("deviceId", String(filters.deviceId));
  if (filters.severity) params.set("severity", filters.severity);
  if (filters.fromDate) params.set("fromDate", filters.fromDate);
  if (filters.toDate) params.set("toDate", filters.toDate);
  if (filters.page != null) params.set("page", String(filters.page));
  if (filters.size != null) params.set("size", String(filters.size));
  if (filters.sort) params.set("sort", filters.sort);

  const query = params.toString();
  return request<Page<AlertDto>>(`/alerts${query ? "?" + query : ""}`);
}

/**
 * GET /alerts/{id}
 * Детали алерта с историей.
 */
export function fetchAlertDetails(id: number | string): Promise<AlertDetailsDto> {
  return request<AlertDetailsDto>(`/alerts/${id}`);
}

/**
 * POST /alerts/{id}/confirm
 * Переход: NEW → ACKNOWLEDGED.
 * Только для роли OPERATOR.
 */
export function confirmAlert(id: number | string): Promise<AlertDto> {
  return request<AlertDto>(`/alerts/${id}/confirm`, { method: "POST" });
}

/**
 * POST /alerts/{id}/start_work
 * Переход: ACKNOWLEDGED → IN_PROGRESS.
 * Только для роли ENGINEER.
 */
export function startWork(id: number | string): Promise<AlertDto> {
  return request<AlertDto>(`/alerts/${id}/start_work`, { method: "POST" });
}

/**
 * POST /alerts/{id}/resolve
 * Переход: IN_PROGRESS → RESOLVED.
 * Только для роли ENGINEER.
 */
export function resolveAlert(id: number | string): Promise<AlertDto> {
  return request<AlertDto>(`/alerts/${id}/resolve`, { method: "POST" });
}

/**
 * POST /alerts/{id}/mark_false_positive
 * Переход: → FALSE_POSITIVE.
 * Только для роли ENGINEER.
 */
export function markFalsePositive(id: number | string): Promise<AlertDto> {
  return request<AlertDto>(
    `/alerts/${id}/mark_false_positive`,
    { method: "POST" }
  );
}
