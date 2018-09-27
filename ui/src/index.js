import {h, app} from "hyperapp"
import devtools from 'hyperapp-redux-devtools'

const state = {
    count: 0
};

const actions = {
    down: value => state => ({count: state.count - value}),
    up: value => state => ({count: state.count + value})
};

const view = (state, actions) => (
    <div>
        <h1>{state.count}</h1>
        <button onclick={() => actions.down(1)}>-</button>
        <button onclick={() => actions.up(1)}>+</button>
    </div>
);

let main;

if (process.env.NODE_ENV !== 'production') {
    main = devtools(app)(state, actions, view, document.body)
} else {
    main = app(state, actions, view, document.body)
}