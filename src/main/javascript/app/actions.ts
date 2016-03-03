import { ITodoAction, VisibilityStateType, IVisibilityAction} from './types'
import {Dispatch} from 'redux';

import fetch from 'isomorphic-fetch'
import {ITodoState} from "./types";

let nextTodoId: number = 0;

export function addTodo(title: string): ITodoAction {
    return {id: nextTodoId++, type: 'TODO_ADD',  title};
}

export function toggleTodo(id: number): ITodoAction {
    return {type: 'TODO_TOGGLE', id};
}

export function setVisibilityFilter(filter: VisibilityStateType): IVisibilityAction {
    return {
        filter: filter,
        type: 'VISIBILITY_FILTER_SET',
    };
}


const SERVER_URL: string = 'http://localhost:9090/api/';

export function fetchTodos(dispatch: Redux.Dispatch) {
    console.log("fetching todos");
    return fetch(SERVER_URL)
        .then(response => response.json())
        .then((todos: ITodoState[]) => dispatch(receiveTodos(todos)));
}


export function receiveTodos(todos: ITodoState[]): ITodoAction {
    console.log('receiving todos: ', todos);
    return {
        type: "TODO_RECEIVE",
        todos: todos
    };
}

