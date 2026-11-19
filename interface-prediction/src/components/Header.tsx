import { Link, useLocation } from "react-router-dom";
import RoleSwitcher from "./RoleSwitcher";

export default function Header() {
  const location = useLocation();

  const navItems = [
    { to: "/", label: "Дашборд" },
    { to: "/alerts", label: "История алертов" },
  ];

  return (
    <header className="app-header">
      <div className="header-left">
        <Link to="/" className="app-logo">
          Predictive Maintenance
        </Link>
        <nav className="app-nav">
          {navItems.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className={`nav-link ${location.pathname === item.to ? "active" : ""}`}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </div>
      <RoleSwitcher />
    </header>
  );
}
