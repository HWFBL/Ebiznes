import {createContext} from "react";

const AppContext = createContext({ user: null, token: null, basket: [] });

export { AppContext }