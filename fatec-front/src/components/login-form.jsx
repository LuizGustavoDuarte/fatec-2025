"use client"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useForm } from "react-hook-form";
import { useProcessingContext } from "@/app/login/page"
import axios from "axios"

export function LoginForm({
  className,
  ...props
}) {

  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm();

  function encodeFileToBase64(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        // Extract Base64 string by removing Data URL prefix
        const base64String = reader.result.split(',')[1];
        resolve(base64String);
      };
      reader.onerror = (error) => reject(error);
      reader.readAsDataURL(file);
    });
  }

  const onSubmit = async (data) => {
    try {
      const base64 = await encodeFileToBase64(data.image[0]);
      props.setFunction([...props.currentState, [base64]])
      let response = await axios.post("http://localhost:8080/api/image/upload", { imageBase64: base64 })
      let recursivePolling = async () => {
        let processes = await axios.get("http://localhost:8080/api/image/get/" + response.data + "/processed");
        console.log(processes)
        if(processes.data) {
          props.setFunction([...props.currentState, [base64, ...processes.data]])
        } else {
          setTimeout(recursivePolling, 1000)
        }
      }

      setTimeout(recursivePolling, 1000)
      
    } catch (e) {
      console.log(e)
    }
    
  };

  return (
    <form className={cn("flex flex-col gap-6", className)} onSubmit={handleSubmit(onSubmit)} {...props}>
      <div className="flex flex-col items-center gap-2 text-center">
        <h1 className="text-2xl font-bold">Escolha uma imagem para realizar processamento</h1>
      </div>
      <div className="grid gap-6">
        <div className="grid gap-2">
          <Input
            id="image"
            type="file"
            accept="image/*"
            {...register("image", { required: "Selecione uma imagem" })}
          />
        </div>
        <Button type="submit" className="w-full">
          Processar
        </Button>
      </div>
    </form>
  );
}




