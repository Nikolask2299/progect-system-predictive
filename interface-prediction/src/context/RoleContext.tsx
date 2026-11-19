import { createContext, useContext, useState, type ReactNode } from "react";
import type { Role } from "../api/types";

interface RoleContextType {
  role: Role;
  setRole: (role: Role) => void;
}

const RoleContext = createContext<RoleContextType | undefined>(undefined);

/**
 * Провайдер контекста роли.
 *
 * ВАЖНО: это только UX-разделение видимости.
 * Реальная проверка прав осуществляется на сервере/шлюзе.
 */
export function RoleProvider({ children }: { children: ReactNode }) {
  const [role, setRole] = useState<Role>("OPERATOR");

  return (
    <RoleContext.Provider value={{ role, setRole }}>
      {children}
    </RoleContext.Provider>
  );
}

/**
 * Хук для чтения текущей роли.
 * @throws Error если вызван вне RoleProvider
 */
export function useRole(): RoleContextType {
  const context = useContext(RoleContext);
  if (context === undefined) {
    throw new Error("useRole must be used within a RoleProvider");
  }
  return context;
}
