import React, {useContext} from "react";
import BaseLayout from "../layouts/BaseLayout";
import {AppContext} from "../utils/AppContext/AppContext";
import Button from "@material-ui/core/Button";

export default function BasketPage() {
    const { getBasketItems, removeProductFromBasket } = useContext(AppContext);

    return (
        <BaseLayout>
            <h2>Basket</h2>
            <pre>{JSON.stringify(getBasketItems)}</pre>
            {getBasketItems.map(basketItem => (
                <Button variant="contained" color="secondary" onClick={() => removeProductFromBasket(basketItem)}>Remove product {basketItem}</Button>
            ))}
        </BaseLayout>
    )
}