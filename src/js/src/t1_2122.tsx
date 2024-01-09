import React, {Component, ComponentType, useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    messages: string[],
    period: number,
    component: ComponentType<{text: string}>
}

function T1_2122({messages, period, component: Component}: props) {
    const [display, setDisplay] = useState("")

    useEffect(() => {
        let i = 0
        setInterval(() => {
            setDisplay(messages[i])
            if (i === messages.length - 1) {
                i = 0
            } else {
                i += 1
            }
        }, period)
    }, [])

    return (
        <div>
            <Component text={display}/>
        </div>
    )
}

const RandomComponent = ({text}: {text: string}) => <p>{text}</p>

const messages = ["Hello", "World", "How", "Are", "You"]

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<T1_2122 messages={messages} period={2000} component={RandomComponent} />)
}