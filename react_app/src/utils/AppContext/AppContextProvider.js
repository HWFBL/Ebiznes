import React, {useState} from "react";
import {AppContext} from "./AppContext";

const cacheUserContext = JSON.parse(localStorage.getItem('app_user_context'));
const cacheBasketContext = JSON.parse(localStorage.getItem('app_basket_context'));

export default function AppContextProvider({children}) {
    const [_userContext, _setUserContext] = useState(cacheUserContext || { user: null, token: null });
    const [_basketContext, _setBasketContext] = useState(cacheBasketContext || { items: [] });
    const [totalprice, setTotalprice] = useState(0)

    const getToken = _userContext.token;
    const getUser = _userContext.user;
    const getBasketItems = _basketContext.items;
    const getTotalPrice = totalprice

    const setUserContext = (token, user) => {
        const temp = { token, user };
        _setUserContext(temp);
        localStorage.setItem('app_user_context', JSON.stringify(temp))
    };

    const setBasketItems = (items) => {
        const temp = {
            ..._basketContext,
            items
        };
        _setBasketContext(temp);
        localStorage.setItem('app_basket_context', JSON.stringify(temp))
    };

    const addProductToBasket = (product_id) => {
        if (!_basketContext.items.includes(product_id)) {
            const items = [..._basketContext.items, product_id];
            setBasketItems(items)
        }
    };

    const setPrice = price => {
        setTotalprice(price)
    }

    const removeProductFromBasket = (product_id) => {
        if (_basketContext.items.includes(product_id)) {
            const items = [..._basketContext.items];
            const index = items.indexOf(product_id);
            if (index > -1) {
                items.splice(index, 1);
            }
            setBasketItems(items)
        }
    };

    const removeAllProductsFromBasket = () => {
        setBasketItems([])
    };


    return (
        <AppContext.Provider value={{getToken, getUser, getBasketItems, getTotalPrice, setPrice, setUserContext, addProductToBasket, removeProductFromBasket, removeAllProductsFromBasket}}>
            {children}
        </AppContext.Provider>
    )
}
