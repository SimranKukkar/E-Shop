import { configureStore } from "@reduxjs/toolkit";
import { productReducer } from "./ProductReducer";
import errorReducer from "./ErrorReducer";
import { cartReducer } from "./CartReducer";
import { authReducer } from "./authReducer";
import { paymentMethodReducer } from "./paymentMethodReducer";

const user = localStorage.getItem("auth")
  ? JSON.parse(localStorage.getItem("auth"))
  : null;

const cartItems = localStorage.getItem("cartItems")
  ? JSON.parse(localStorage.getItem("cartItems"))
  : [];

const initialState = {
  carts: { cart: cartItems },
  auth: { user: user },
};
const store = configureStore({
  reducer: {
    products: productReducer,
    errors: errorReducer,
    carts: cartReducer,
    auth: authReducer,
    payment: paymentMethodReducer,
  },
  preloadedState: initialState,
});
export default store;
