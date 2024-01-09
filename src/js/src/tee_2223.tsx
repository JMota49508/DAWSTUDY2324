import React, {useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    f: () => Promise<string>
}

function TEE_2223(props: props) {
    const [result, setResult] = useState("")
    const [pending, setPending] = useState(false)

    const handleClick = async () => {
        setPending(true)
        try {
            const result = await props.f()
            setResult(result)
        } catch (e) {
            setResult(e.message)
        } finally {
            setPending(false)
        }
    }

    return (
        <div>
            <p><textarea value={result} readOnly/></p>
            <p>
                <button disabled={pending} onClick={handleClick}>Get</button>
            </p>
        </div>
    )
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<TEE_2223 f={async () => {
        const response = await fetch('https://httpbin.org/delay/6')
        return response.text()
    }}/>)
}