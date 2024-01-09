import React, {useEffect, useState} from 'react'
import {createRoot} from "react-dom/client";

type props = {
    list: string[]
}

type listType = {
    url: string
    text: string
}

function TEE_2122(props: props) {
    const [list, setList] = useState<listType[]>([])

    useEffect(() => {
        doFetch()
    }, [props.list])

    async function doFetch() {
        for (const currItem of props.list) {
            setList((x) => [...x, {url: currItem, text: "..."}])
            try {
                const fetched = await fetch(currItem)
                setList((x) => {
                    const newList = [...x]
                    newList[x.length - 1].text = fetched.status.toString()
                    return newList
                })
            } catch (e) {
                setList((x) => {
                    const newList = [...x]
                    newList[x.length - 1].text = e.message
                    return newList
                })
            }
        }
    }

    return (
        <div>
            <ul>
                {list.map((item, index) => (
                    <li key={index}>{item.url},{item.text}</li>
                ))}
            </ul>
        </div>
    )
}

const list = [
    'https://httpbin.org/get',
    'https://httpbin.or/delay/2',
    'https://httpbin.org/delay/5'
]

export function demo() {
    const root = createRoot(document.getElementById("container"))
    root.render(<TEE_2122 list={list}/>)
}