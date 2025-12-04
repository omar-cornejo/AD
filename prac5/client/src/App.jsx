import { Routes, Route } from 'react-router-dom'
import ReelsView from './components/ReelsView'
import UploadVideo from './components/UploadVideo'
import './App.css'

function App() {
  return (
    <div className="app">
      <Routes>
        <Route path="/" element={<ReelsView />} />
        <Route path="/upload" element={<UploadVideo />} />
      </Routes>
    </div>
  )
}

export default App
