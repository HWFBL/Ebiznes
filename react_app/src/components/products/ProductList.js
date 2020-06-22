import React, {useState} from "react";
import ProductListItem from "./ProductListItem";
import Grid from "@material-ui/core/Grid";

export default function ProductList() {
    const [products, setProducts] = useState([...Array(6*4).keys()]);
    return (
        <Grid container spacing={2}>
            {products.map(p => (
                <ProductListItem id={p}/>
            ))}
        </Grid>
    )
}