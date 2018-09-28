import $ from "jquery";
import {h, app} from "hyperapp"
import devtools from 'hyperapp-redux-devtools'

const actions = {
    down: value => state => ({count: state.count - value}),
    up: value => state => ({count: state.count + value})
};

const appContainer = document.querySelector("#root > [data-app]");

const stateContainer = document.querySelector("#root > [data-state]");

const state = JSON.parse(stateContainer.innerHTML);

const view = (state, actions) => (
    <ul className="list-group">
        {state.films.map(film => (

            <li className="list-group-item">
                <a href="/film/{film.id}" className="btn btn-danger js-delete badge">Delete</a>
                <a className="hideOverflow" href="/film/{film.id}">{film.name}</a>


                <span className="glyphicon glyphicon-star" aria-hidden="true"></span>

            </li>
        ))}
    </ul>
);

let main;

if (process.env.NODE_ENV !== 'production') {
    main = devtools(app)(state, actions, view, appContainer)
} else {
    main = app(state, actions, view, appContainer)
}

setTimeout(function () {
    $.getJSON('/api/film', function (data) {
        state.films = data;
        console.log(state);
    });
}, 3000);