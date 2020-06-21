import React from "react";
import BaseLayout from "../layouts/BaseLayout";
import ProductList from "../components/products/ProductList";

export default function HomePage() {
    return (
        <BaseLayout>
            <ProductList/>
        </BaseLayout>
    )
}