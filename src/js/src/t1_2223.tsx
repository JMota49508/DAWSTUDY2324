import React, {useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    uri: string,
    period: number
}

function T1_2223(props: props) {
    const [data, setData] = useState("")

    useEffect(() => {
        const tid = setInterval(() => {
            doFetch()
        }, props.period)
        return () => clearInterval(tid)
    }, [props.uri, props.period])

    async function doFetch() {
        try {
            const fetched = await fetch(props.uri)
            const text = await fetched.text()
            setData(text)
        } catch (e) {
            setData(e.message)
        }
    }

    return (
        <div>
            <textarea value={data} readOnly={true}/>
        </div>
    )
}

function useCounter(initial: number): [observed: number, inc: () => void, dec: () => void] {
    const [count, setCount] = useState(initial)
    const inc = () => setCount((x) => x + 1)
    const dec = () => setCount((x) => x - 1)
    return [count, inc, dec]
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<T1_2223 uri={"https://httpbin.org/delay/5"} period={5000}/>)
}