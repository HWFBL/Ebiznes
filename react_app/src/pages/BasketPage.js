import React, {useContext, useEffect, useState} from "react";
import BaseLayout from "../layouts/BaseLayout";
import {AppContext} from "../utils/AppContext/AppContext";
import Button from "@material-ui/core/Button";
import axios from 'axios';
import ProductListItem from "../components/products/ProductListItem";
import Grid from "@material-ui/core/Grid";
import {Box} from "@material-ui/core";
import {Link as RouterLink } from "react-router-dom";






export default function BasketPage() {
    const { getBasketItems, getUser } = useContext(AppContext);
    const [product, setProducts] = useState([])

    const [price, setPrice] = useState(0)


    useEffect(
        () => {
            const resData = []
            const sendIds = async () => {
                try {
                    let params = getBasketItems

                   for( let i = 0; i < params.length; i++) {
                       const res = await axios.get(`/products/${params[i]}`)
                       resData.push(res.data)
                   }
                    setProducts(resData)
                    let tabOfNumbers = product.map(p => p.price)
                    setPrice(tabOfNumbers)
                    console.log(product)
                } catch (e) {
                    console.log(e)
                }

            }
            sendIds()
        }, []
    )



    let tabOfNumbers = product.map(p => p.price)
    let total = tabOfNumbers.reduce((a, b) => a + b, 0)
    const sendOrder = async () => {
        const pars = {
            orderId: total, payment: total, dispute: getUser.firstName, status: 'dds'
        }

        axios.post('/orderItem', pars )
    }

    return (
        <BaseLayout>
            <h2>Koszyk</h2>
            <Grid container spacing={2}>
                {product.map(p => (
                    <ProductListItem id={p} />
                ))}
            </Grid>

            <Box mt={5}>
                Do zapłaty: <Button color="secondary" size="large" variant="outlined">{tabOfNumbers.reduce((a, b) => a + b, 0) } PLN</Button>

                    <Button color="primary" size="large" variant="contained" component={RouterLink} to='/profile' onClick={() => sendOrder()}> Zapłać </Button>


            </Box>

        </BaseLayout>
    )
}
