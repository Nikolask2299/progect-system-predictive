import { useRole } from "../context/RoleContext";

/**
 * Переключатель роли в шапке приложения.
 * Позволяет переключаться между OPERATOR и ENGINEER
 * для демонстрации ролевой модели.
 */
export default function RoleSwitcher() {
  const { role, setRole } = useRole();

  const nextRole = role === "OPERATOR" ? "ENGINEER" : "OPERATOR";

  return (
    <div className="role-switcher">
      <span className="role-label">Роль:</span>
      <button
        className="role-button"
        onClick={() => setRole(nextRole)}
        title="Переключить роль"
      >
        {role === "OPERATOR" ? "Оператор" : "Инженер"}
        <span className="role-arrow"> → {nextRole === "OPERATOR" ? "Оператор" : "Инженер"}</span>
      </button>
    </div>
  );
}
