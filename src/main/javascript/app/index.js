import * as ReactDOM from 'react-dom';
import * as React from 'react';
import { Provider, connect } from 'react-redux';
const SERVER_URL = 'http://localhost:9090/api/';
// Action Creators
// ===============
// export const RECEIVE_TODOS: string = 'RECEIVE_TODOS';
//
// function receiveTodos(json: string): ITodoAction {
//     return {
//         type: RECEIVE_TODOS,
//     };
// };
//
// function fetchTodos() {
//     return (dispatch) => {
//         return fetch(SERVER_URL)
//             .then(req => req.json)
//             .then(json => dispatch(receiveTodos))
//    };
// }
let nextTodoId = 0;
function addTodo(text) {
    return { id: nextTodoId++, type: 'TODO_ADD', text };
}
function toggleTodo(id) {
    return { type: 'TODO_TOGGLE', id };
}
function setVisibilityFilter(filter) {
    return {
        filter: filter,
        type: 'VISIBILITY_FILTER_SET',
    };
}
// Reducers
// ========
function todo(state, action) {
    switch (action.type) {
        case 'ADD_TODO':
            return {
                completed: false,
                id: action.id,
                text: action.text,
            };
        case 'TOGGLE_TODO':
            if (state.id !== action.id) {
                return state;
            }
            else {
                return Object.assign({}, state, { completed: !state.completed });
            }
        default:
            return state;
    }
}
function todos(state = [], action) {
    switch (action.type) {
        case 'TODO_ADD':
            return [
                ...state,
                todo(undefined, action),
            ];
        case 'TODO_TOGGLE':
            return state.map(t => todo(t, action));
        default:
            return state;
    }
}
function visibilityFilter(state = 'SHOW_ALL', action) {
    switch (action.type) {
        case 'VISIBILITY_FILTER_SET':
            return action.filter;
        default:
            return state;
    }
}
import { createStore, combineReducers } from 'redux';
const todoApp = combineReducers({
    todos,
    visibilityFilter,
});
function getVisibleTodos(todos, filter) {
    switch (filter) {
        case 'SHOW_ALL':
            return todos;
        case 'SHOW_COMPLETED':
            return todos.filter(t => t.completed);
        case 'SHOW_ACTIVE':
            return todos.filter(t => !t.completed);
        default:
            return todos;
    }
}
function Todo({ onClick, completed, text }) {
    return (React.createElement("li", {onClick: onClick, style: { textDecoration: completed ? 'line-through' : 'none' }}, text));
}
function TodoList({ todos, onTodoClick }) {
    return (React.createElement("ul", null, todos.map(todo => React.createElement(Todo, React.__spread({key: todo.id}, todo, {onClick: () => onTodoClick(todo.id)})))));
}
function mapStateToTodoListProps(state) {
    return {
        todos: getVisibleTodos(state.todos, state.visibilityFilter),
    };
}
function mapDispatchToTodoListProps(dispatch) {
    return {
        onTodoClick: (id) => dispatch(toggleTodo(id)),
    };
}
const VisibleTodoList = connect(mapStateToTodoListProps, mapDispatchToTodoListProps)(TodoList);
function AddTodoSimple({ dispatch }) {
    let input;
    return (React.createElement("div", null, 
        React.createElement("input", {ref: node => { input = node; }}), 
        React.createElement("button", {onClick: () => {
            dispatch(addTodo(input.value));
            input.value = '';
        }}, "Add Todo")));
}
const AddTodo = connect()(AddTodoSimple);
function Link({ active, onClick, children }) {
    if (active) {
        return React.createElement("span", null, children);
    }
    return (React.createElement("a", {href: '#', onClick: onClick}, children));
}
function mapStateToLinkProps(state, ownProps) {
    return {
        active: ownProps.filter === state.visibilityFilter,
    };
}
function mapDispatchToLinkProps(dispatch, ownProps) {
    return {
        onClick: () => dispatch(setVisibilityFilter(ownProps.filter)),
    };
}
const FilterLink = connect(mapStateToLinkProps, mapDispatchToLinkProps)(Link);
function Footer() {
    return (React.createElement("p", null, 
        "Show:", 
        ' ', 
        " ", 
        React.createElement(FilterLink, {filter: 'SHOW_ALL'}, "All"), 
        ' ', 
        " ", 
        React.createElement(FilterLink, {filter: 'SHOW_ACTIVE'}, "Active"), 
        ' ', 
        " ", 
        React.createElement(FilterLink, {filter: 'SHOW_COMPLETED'}, "Completed")));
}
const TodoApp = () => (React.createElement("div", null, 
    React.createElement(AddTodo, null), 
    React.createElement(VisibleTodoList, null), 
    React.createElement(Footer, null)));
ReactDOM.render(React.createElement(Provider, {store: createStore(todoApp)}, 
    React.createElement(TodoApp, null)
), document.getElementById('app'));
