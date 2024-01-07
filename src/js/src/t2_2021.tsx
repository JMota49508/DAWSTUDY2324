import {createRoot} from "react-dom/client";
import React from "react";

function T2_2021() {
    const [going, setGoing] = React.useState(false)
    const [list, setList] = React.useState([])
    const [startTime, setStartTime] = React.useState(0)

    const handleStart = () => {
        if (going) {
            setGoing(false)
            setList([])
        } else {
            setGoing(true)
            setStartTime(Date.now())
        }
    }

    const handleLap = () => {
        setList((it) => [...it, (Date.now() - startTime)/1000])
    }

    return (
        <div>
            <p>
                <button onClick={handleStart}>Start</button>
                <button onClick={handleLap} disabled={!going}>Lap</button>
            </p>
            <p>
                <textarea value={list} readOnly={true} cols={80} rows={20}/>
            </p>
        </div>
    )
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<T2_2021/>)
}