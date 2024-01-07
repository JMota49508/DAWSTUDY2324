import React, {useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

function TEE_2021() {
    const [counter, setCounter] = useState(0)
    const [text, setText] = useState("")
    const [intervalValue, setIntervalValue] = useState(-1)


    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setText(e.target.value)
    }

    const handleClick = () => {
        if (text === "") {
            setIntervalValue(-1)
            return
        }
        const value = parseInt(text)
        if (isNaN(value)) { return }
        setIntervalValue(value)
    }

    useEffect(() => {
        if (intervalValue >= 0){
            const tid = setInterval(() => {
                setCounter(x => x + 1)
            }, intervalValue)
            return () => clearInterval(tid)
        }
    })

    return (
        <div>
            <input value={text} onChange={handleChange}></input>
            <button onClick={handleClick}>Start</button>
            <p>Counter Value: {counter}</p>
        </div>
    )
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<TEE_2021/>)
}