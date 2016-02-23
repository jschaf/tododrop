var ReactDOM = require('react-dom');
var React = require('react');
var react_redux_1 = require('react-redux');
const SERVER_URL = 'http://localhost:9090/api/';
//function checkStatus(response) {
//    if (response.status >= 200 && response.status < 300) {
//        return response;
//    } else {
//        let error = new Error(response.statusText);
//        error.response = response;
//        throw error;
//    }
//}
function parse(response) {
    return response.json();
}
// Action Creators
// ===============
exports.RECEIVE_TODOS = 'RECEIVE_TODOS';
const receiveTodos = (json) => {
    return {
        type: exports.RECEIVE_TODOS
    };
};
const fetchTodos = () => {
    return (dispatch) => {
        return fetch(SERVER_URL)
            .then(req => req.json)
            .then(json => dispatch(receiveTodos));
    };
};
let nextTodoId = 0;
const addTodo = (text) => {
    return { type: 'ADD_TODO', id: nextTodoId++, text };
};
const toggleTodo = (id) => {
    return { type: 'TOGGLE_TODO', id };
};
const setVisibilityFilter = (filter) => {
    return {
        type: 'SET_VISIBILITY_FILTER',
        filter
    };
};
// Reducers
// ========
const todo = (state, action) => {
    switch (action.type) {
        case 'ADD_TODO':
            return {
                id: action.id,
                text: action.text,
                completed: false
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
};
const todos = (state = [], action) => {
    switch (action.type) {
        case 'ADD_TODO':
            return [
                ...state,
                todo(undefined, action)
            ];
        case 'TOGGLE_TODO':
            return state.map(t => todo(t, action));
        default:
            return state;
    }
};
const visibilityFilter = (state = 'SHOW_ALL', action) => {
    switch (action.type) {
        case 'SET_VISIBILITY_FILTER':
            return action.filter;
        default:
            return state;
    }
};
var redux_1 = require('redux');
const todoApp = redux_1.combineReducers({
    todos,
    visibilityFilter
});
const getVisibleTodos = (todos, filter) => {
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
};
const Todo = ({ onClick, completed, text }) => (React.createElement("li", {"onClick": onClick, "style": { textDecoration: completed ? 'line-through' : 'none' }}, text));
const TodoList = ({ todos, onTodoClick }) => (React.createElement("ul", null, todos.map(todo => React.createElement(Todo, React.__spread({"key": todo.id}, todo, {"onClick": () => onTodoClick(todo.id)})))));
const mapStateToTodoListProps = (state) => {
    return {
        todos: getVisibleTodos(state.todos, state.visibilityFilter)
    };
};
const mapDispatchToTodoListProps = (dispatch) => {
    return {
        onTodoClick: id => dispatch(toggleTodo(id))
    };
};
var react_redux_2 = require('react-redux');
const VisibleTodoList = react_redux_2.connect(mapStateToTodoListProps, mapDispatchToTodoListProps)(TodoList);
VisibleTodoList.contextTypes = {
    store: React.PropTypes.object
};
const AddTodoSimple = ({ dispatch }) => {
    let input;
    return (React.createElement("div", null, React.createElement("input", {"ref": node => { input = node; }}), React.createElement("button", {"onClick": () => {
        dispatch(addTodo(input.value));
        input.value = '';
    }}, "Add Todo")));
};
const AddTodo = react_redux_2.connect()(AddTodoSimple);
const Link = ({ active, onClick, children }) => {
    if (active) {
        return React.createElement("span", null, children);
    }
    return (React.createElement("a", {"href": "#", "onClick": onClick}, children));
};
const mapStateToLinkProps = (state, ownProps) => {
    return {
        active: ownProps.filter === state.visibilityFilter
    };
};
const mapDispatchToLinkProps = (dispatch, ownProps) => {
    return {
        onClick: () => dispatch(setVisibilityFilter(ownProps.filter))
    };
};
const FilterLink = react_redux_2.connect(mapStateToLinkProps, mapDispatchToLinkProps)(Link);
const Footer = () => (React.createElement("p", null, "Show:", ' ', " ", React.createElement(FilterLink, {"filter": 'SHOW_ALL'}, "All"), ' ', " ", React.createElement(FilterLink, {"filter": 'SHOW_ACTIVE'}, "Active"), ' ', " ", React.createElement(FilterLink, {"filter": 'SHOW_COMPLETED'}, "Completed")));
const TodoApp = () => (React.createElement("div", null, React.createElement(AddTodo, null), React.createElement(VisibleTodoList, null), React.createElement(Footer, null)));
ReactDOM.render(React.createElement(react_redux_1.Provider, {"store": redux_1.createStore(todoApp)}, React.createElement(TodoApp, null)), document.getElementById('root'));
