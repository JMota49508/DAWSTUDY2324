import React, {useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    f: () => Promise<string>
}

function T2_2223(props: props) {
    const [result, setResult] = useState("")
    const [counter, setCounter] = useState(0)
    const [pending, setPending] = useState(false)

    useEffect(() => {
        if (pending) {
            const tid = setInterval(() => {
                setCounter((x) => x + 1)
            }, 100)
            return () => clearInterval(tid)
        }
    }, [pending])

    useEffect(() => {
        setPending(true)
        awaitPromise()

        async function awaitPromise() {
            try {
                const result = await props.f()
                setResult(result)
            } catch (e) {
                setResult(e.message)
            } finally {
                setPending(false)
            }
        }
    }, [props.f])

    return (
        <div>
            <p>Response: {result}</p>
            <p>Counter: {counter}</p>
        </div>
    )
}

function useInput(initial: string): [currentValue: string, changeHandler: (ev: React.ChangeEvent<HTMLInputElement>) => void] {
    const [value, setValue] = useState<string>(initial)

    const changeValue = (ev: React.ChangeEvent<HTMLInputElement>) => {
        setValue(ev.target.value)
    }

    return [value, changeValue]
}


function Example() {
    const [value, handler] = useInput("")
    return (
        <input type="text" value={value} onChange={handler} />
    );
}

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(
        <T2_2223 f={async () => {
            const response = await fetch('https://httpbin.org/delay/6')
            return response.text()
        }}/>
    )
    //root.render(<Example />)
}