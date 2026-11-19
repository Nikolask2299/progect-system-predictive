import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import PredictionApp from "./Prediction-App.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <PredictionApp/>
  </StrictMode>,
)
