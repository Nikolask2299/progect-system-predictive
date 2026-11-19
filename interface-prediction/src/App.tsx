import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { RoleProvider } from "./context/RoleContext";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";
import AssetDetails from "./pages/AssetDetails";
import AlertHistory from "./pages/AlertHistory";

export default function App() {
  return (
    <BrowserRouter>
      <RoleProvider>
        <div className="app-layout">
          <Header />
          <main className="app-main">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/assets/:deviceId" element={<AssetDetails />} />
              <Route path="/alerts" element={<AlertHistory />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </RoleProvider>
    </BrowserRouter>
  );
}
