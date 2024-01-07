import React, {useState} from 'react'
import {createRoot} from "react-dom/client";

function T1_2021() {
    const [uri, setUri] = useState("")
    const [response, setResponse] = useState("")
    const [loading, setLoading] = useState(false)

    const handleUriChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setUri(e.target.value)
    }

    const handleFetch = async () => {
        setLoading(true)
        const res = await fetch(uri)
        const text = await res.text()
        setResponse(text)
        setLoading(false)
    }

    return (
        <div>
            <p>
                <label>
                    Uri:
                    <input type="text" value={uri} onChange={handleUriChange}/>
                </label>
                <button onClick={handleFetch} disabled={loading}>Fetch</button>
            </p>
            <p>
                <textarea value={response} readOnly={true} cols={80} rows={20}/>
            </p>
        </div>
    )
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<T1_2021/>)
}