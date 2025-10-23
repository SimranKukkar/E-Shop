import { FadeLoader } from "react-spinners";

const Loader = ({ text }) => {
  return (
    <div className="flex justify-center items-center w-full h-[350px]">
      <div className="flex flex-col items-center gap-1">
        <FadeLoader color="red" />
        <p className="text-slate-800">{text ? text : "Please wait...."}</p>
      </div>
    </div>
  );
};
export default Loader;
