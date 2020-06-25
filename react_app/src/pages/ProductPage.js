import React, {useContext, useEffect, useState} from "react";
import BaseLayout from "../layouts/BaseLayout";
import {useParams} from "react-router-dom";
import axios from 'axios';
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {AppContext} from "../utils/AppContext/AppContext";
import { spacing } from '@material-ui/system';
import Button from "@material-ui/core/Button";
import Box from "@material-ui/core/Box";

export default function ProductPage() {

    const {addProductToBasket} = useContext(AppContext)

    const urlParams = useParams();
    const productId = urlParams.id;
    const [product, setProduct] = useState([])
    const [photo, setPhoto] = useState([])

    useEffect(() => {
    const getProduct = async () => {
        try {
            let res = await axios.get(`/products/${productId}`)
            let products = res.data
            setProduct(products)
            let res2 = await axios.get(`/photos/${products.photo}`)
            let photos = res2.data
            setPhoto(photos)
        } catch (e) {
            console.log(e)
        }

    }
    getProduct();
    }, [])

    return (
        <BaseLayout>
<Grid container>
    <Grid item xs={12}><h2> {product.name}</h2></Grid>
<Box mb={10} > </Box>
    <Grid item xs={6}>
        <Box mx={10}>
            <img src={photo.link} width={"400px"} height={"400px"} alt=""/>

        </Box>
    </Grid>

    <Grid item xs={6}>
        <Typography variant={"h6"}>Opis Produktu</Typography>
        <Box my={3}></Box>
        <Typography variant={"subtitle1"}>
            {product.description}
        </Typography>
        <Box mt={4}>
            <Button variant="contained" color="primary" onClick={() => addProductToBasket(productId)}>Dodaj do koszyka</Button>
    <Box mt={3}>
        <Typography  variant={"h5"}>
            {product.price} PLN
        </Typography>
    </Box>
        </Box>

    </Grid>
</Grid>


        </BaseLayout>
    )
}
