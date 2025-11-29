import { Routes, Route } from 'react-router-dom'
import ReelsView from './components/ReelsView'
import './App.css'

function App() {
  return (
    <div className="app">
      <Routes>
        <Route path="/" element={<ReelsView />} />
      </Routes>
    </div>
  )
}

export default App
