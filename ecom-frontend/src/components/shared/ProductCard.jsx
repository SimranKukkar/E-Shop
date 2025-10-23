import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import ProductViewModal from "./ProductViewModal";
import truncateText from "../../utils/truncateText";
import toast from "react-hot-toast";
import { addToCart } from "../../store/actions";
import { useDispatch } from "react-redux";
import { formatPrice } from "../../utils/formatPrice";

const ProductCard = ({
  productId,
  productName,
  image,
  description,
  quantity,
  price,
  discount,
  specialPrice,
  about = false,
}) => {
  const [openProductViewModal, setOpenProductViewModal] = useState(false);
  const btnLoader = false;
  const [selectedViewProduct, setSelectedViewProduct] = useState("");
  const isAvailable = quantity && Number(quantity) > 0;
  const dispatch = useDispatch();
  const handleProductView = (product) => {
    if (!about) {
      setSelectedViewProduct(product);
      setOpenProductViewModal(true);
    }
  };
  const addToCartHandler = (cartItems) => {
    dispatch(addToCart(cartItems, toast));
  };
  return (
    <div className="border rounded-lg shadow-xl overflow-hidden transition-shadow duration-300">
      <div
        onClick={() =>
          handleProductView({
            id: productId,
            productName,
            image,
            description,
            quantity,
            price,
            discount,
            specialPrice,
          })
        }
        className="w-full overflow-hidden aspect-[3/2]"
      >
        <img
          className="w-full h-full cursor-pointer transition-transform duration-300 transform hover:scale-105"
          src={image}
          alt={productName}
        />
      </div>
      <div className="p-4">
        <h2
          onClick={() => {
            handleProductView({
              id: productId,
              productName,
              image,
              description,
              quantity,
              price,
              discount,
              specialPrice,
            });
          }}
          className="text-lg font-semibold mb-2 cursor-pointer"
        >
          {truncateText(productName, 50)}
        </h2>
        <div className="min-h-20 max-h-20">
          <p className="text-gray-600 text-sm">
            {truncateText(description, 80)}
          </p>
        </div>
        {!about && (
          <div className="flex items-center justify-between">
            {specialPrice ? (
              <div className="flex flex-col">
                <span className="text-gray-400 line-through">
                  {formatPrice(Number(price))}
                </span>
                <span className="text-xl font-bold text-slate-700">
                  {formatPrice(Number(specialPrice))}
                </span>
              </div>
            ) : (
              <span className="text-xl font-bold text-slate-700">
                {"   "}
                {formatPrice(Number(price))}
              </span>
            )}
            <button
              disabled={!isAvailable || btnLoader}
              onClick={() =>
                addToCartHandler({
                  productId,
                  productName,
                  image,
                  description,
                  quantity,
                  price,
                  specialPrice,
                })
              }
              className={`bg-blue-500 ${
                isAvailable
                  ? "opacity-100 hover:bg-blue-600 cursor-pointer"
                  : "opacity-70"
              } text-white py-2 px-3 rounded-lg items-center transition-colors duration-300 w-36 flex justify-center`}
            >
              <FaShoppingCart className="mr-2" />
              {isAvailable ? "Add to Cart" : "Out of Stock"}
            </button>
          </div>
        )}
      </div>
      <ProductViewModal
        open={openProductViewModal}
        setOpen={setOpenProductViewModal}
        product={selectedViewProduct}
        isAvailable={isAvailable}
      />
    </div>
  );
};
export default ProductCard;
