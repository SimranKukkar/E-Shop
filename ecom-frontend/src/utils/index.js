import {
  FaBoxOpen,
  FaHome,
  FaShoppingCart,
  FaStore,
  FaThList,
} from "react-icons/fa";
import {
  bannerImageOne,
  bannerImageThree,
  bannerImageTwo,
  productImageOne,
  productImageTwo,
  productImageThree,
} from "./constant";

export const bannerLists = [
  {
    id: 1,
    image: bannerImageTwo,
    title: "Home Comfort",
    subtitle: "Living Room",
    description: "Upgrade your space with cozy and stylish sofas",
  },
  {
    id: 2,
    image: bannerImageThree,
    title: "Entertainment Hub",
    subtitle: "Smart TV",
    description: "Experience the latest in home entertainment",
  },
  {
    id: 3,
    image: bannerImageOne,
    title: "Playful Picks",
    subtitle: "Shopping",
    description: "Bright and fun styles, up to 20% off",
  },
];
export const products = [
  {
    image: productImageOne,
    productName: "iPhone 13 Pro Max",
    description:
      "The iPhone 13 Pro Max offers exceptional performance with its A15 Bionic chip, stunning Super Retina XDR display, and advanced camera features for breathtaking photos.",
    specialPrice: 720,
    price: 780,
  },
  {
    image: productImageTwo,
    productName: "Samsung Galaxy S21",
    description:
      "Experience the brilliance of the Samsung Galaxy S21 with its vibrant AMOLED display, powerful camera, and sleek design that fits perfectly in your hand.",
    specialPrice: 699,
    price: 799,
  },
  {
    image: productImageThree,
    productName: "Google Pixel 6",
    description:
      "The Google Pixel 6 boasts cutting-edge AI features, exceptional photo quality, and a stunning display, making it a perfect choice for Android enthusiasts.",
    price: 599,
    specialPrice: 400,
  },
];

/*export const adminNavigation = [
  {
    name: "Dashboard", 
    href: "/admin", 
    icon: FaHome, 
    current: true 
  }, {
    name: "Orders", 
    href: "/admin/orders", 
    icon: FaShoppingCart
  }, {
    name: "Products", 
    href: "/admin/products", 
    icon: FaBoxOpen
  }, {
    name: "Categories", 
    href: "/admin/categories", 
    icon: FaThList
  }, {
    name: "Sellers", 
    href: "/admin/sellers", 
    icon: FaStore 
  }
];


export const sellerNavigation = [
  {
    name: "Orders", 
    href: "/admin/orders", 
    icon: FaShoppingCart,
    current: true 
  }, {
    name: "Products", 
    href: "/admin/products", 
    icon: FaBoxOpen
  }
];*/
