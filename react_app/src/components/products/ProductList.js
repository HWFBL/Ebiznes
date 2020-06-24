import React, {useEffect, useState} from "react";
import ProductListItem from "./ProductListItem";
import Grid from "@material-ui/core/Grid";
import axios from 'axios';


export default function ProductList() {
    const [products, setProducts] = useState([]);
    // const [photoID, setPhotoID] = useState([])
    useEffect( () => {
        const getData = async () => {
            try {
                let res = await axios.get('/products')
                let products2 = res.data;
                // products = products.map(p => p.name)
                setProducts(products2)
                console.log(products2)
                // let photoID = products.map(p => p.photo)
                // setPhotoID(photoID)
            } catch (e) {
                console.error(e);
            }
        };
        getData();
    }, [])
    return (
        // <div>{JSON.stringify(products)}</div>
        <Grid container spacing={2}>
            {products.map(p => (
                <ProductListItem id={p} />
            ))}
        </Grid>
    )
}
