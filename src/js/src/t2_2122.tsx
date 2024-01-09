import React, {Component, ComponentType, useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    uri: string,
    period: number
}

function T2_2122(props: props) {
    const [status, setStatus] = useState("")
    const [body, setBody] = useState("")

    useEffect(() => {
        doFetch(props.uri)

    }, [props.uri, props.period])

    async function doFetch(uri: string) {
        try {
            const startTime = Date.now()
            const response = await fetch(uri, {method: "GET"})
            const text = await response.text()
            const elapsedTime = Date.now() - startTime
            if (elapsedTime > props.period) {
                setStatus("Timeout")
            } else {
                setStatus(`${response.status}`)
                setBody(elapsedTime + " ms")
            }
        } catch (e) {
            setStatus(`${e.status}`)
            setBody(e.message)
        }
    }

    return (
        <div>
            <p>Status: {status}</p>
            <p>{body}</p>
        </div>
    )
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<T2_2122 uri={"https://httpbin.org/delay/6"} period={5000}/>)
}