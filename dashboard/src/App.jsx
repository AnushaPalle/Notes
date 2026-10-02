import React from 'react';
import { useDashboardViewModel } from './mvvm/useDashboardViewModel.js';
import { DashboardView } from './mvvm/DashboardView.jsx';

export default function App() {
  const viewModel = useDashboardViewModel();
  return <DashboardView {...viewModel} />;
}
