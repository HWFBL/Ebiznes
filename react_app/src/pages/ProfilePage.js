import React, {useContext} from "react";
import BaseLayout from "../layouts/BaseLayout";
import {AppContext} from "../utils/AppContext/AppContext";
import TextareaAutosize from "@material-ui/core/TextareaAutosize";
import Button from "@material-ui/core/Button";
import Toolbar from "@material-ui/core/Toolbar";

export default function ProfilePage() {
    const {
        getToken,
        getUser,
        getBasketItems,
        setUserContext,
        addProductToBasket,
        removeProductFromBasket,
        removeAllProductsFromBasket
    } = useContext(AppContext);

    const JsonContext = ({value}) => (<pre>{JSON.stringify(value)}</pre>);

    const sampleUser = {
        firstName: 'Jan',
        lastName: 'Kowalski',
        email: 'jankowalski@gmail.com'
    };

    return (
        <BaseLayout>
            <h2>Profile page (App Context) - debug</h2>

            <h4>Token</h4>
            <Button variant="contained" color="primary" onClick={() => setUserContext('sample token 2', getUser)}>Set token</Button>
            <Button variant="contained" color="secondary" onClick={() => setUserContext(null, getUser)}>Remove token</Button>
            <JsonContext value={getToken}/>

            <h4>User</h4>
            <Button variant="contained" color="primary" onClick={() => setUserContext(getToken, sampleUser)}>Set sample user</Button>
            <Button variant="contained" color="secondary" onClick={() => setUserContext(getToken, null)}>Remove user</Button>
            <JsonContext value={getUser}/>

            <h4>Basket</h4>
            <Button variant="contained" color="primary" onClick={() => addProductToBasket('1')}>Add product 1</Button>
            <Button variant="contained" color="primary" onClick={() => addProductToBasket('2')}>Add product 2</Button>
            <Button variant="contained" color="primary" onClick={() => addProductToBasket('3')}>Add product 3</Button>
            <Button variant="contained" color="secondary" onClick={() => removeProductFromBasket('1')}>Remove product 1</Button>
            <Button variant="contained" color="secondary" onClick={() => removeProductFromBasket('2')}>Remove product 2</Button>
            <Button variant="contained" color="secondary" onClick={() => removeProductFromBasket('3')}>Remove product 3</Button>
            <Button variant="contained" color="secondary" onClick={() => removeAllProductsFromBasket()}>Remove basket</Button>
            <JsonContext value={getBasketItems}/>
        </BaseLayout>
    )
}