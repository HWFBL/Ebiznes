import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import CssBaseline from '@material-ui/core/CssBaseline';
import {BrowserRouter, Redirect, Route, Switch} from "react-router-dom";
import * as serviceWorker from './serviceWorker';
import HomePage from "./pages/HomePage";
import {ThemeProvider} from '@material-ui/core/styles';
import theme from "./theme";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import TodoPage from "./pages/TodoPage";
import AppContextProvider from "./utils/AppContext/AppContextProvider";
import ProfilePage from "./pages/ProfilePage";
import NotFoundPage from "./pages/NotFound";
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:9000/api/';
axios.defaults.responseType = 'json';

axios.interceptors.request.use((cfg) => {
    const appContext = JSON.parse(localStorage.getItem('app_context'));

    if (appContext && appContext.user && appContext.token) {
        cfg.headers.common['X-Auth-Token'] = appContext.token
    }

    return cfg;
});


const app = (
    <ThemeProvider theme={theme}>
        <CssBaseline/>
        <AppContextProvider>
            <BrowserRouter>
                <Switch>
                    <Route exact path="/"><HomePage/></Route>
                    <Route exact path="/login"><LoginPage/></Route>
                    <Route exact path="/register"><RegisterPage/></Route>
                    <Route exact path="/profile"><ProfilePage/></Route>

                    <Route exact path="/basket"><TodoPage/></Route>
                    <Route exact path="/product/:id"><TodoPage/></Route>
                    <Route exact path="/categories"><TodoPage/></Route>
                    <Route exact path="/category/:id"><TodoPage/></Route>

                    <Route exact path="/404"><NotFoundPage/></Route>
                    <Redirect to="/404"/>
                </Switch>
            </BrowserRouter>
        </AppContextProvider>
    </ThemeProvider>
);


ReactDOM.render(app, document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
