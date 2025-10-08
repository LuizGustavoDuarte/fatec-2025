"use client"
import { GalleryVerticalEnd } from "lucide-react"
import LiaxLogo from '@/app/LiaxLogo.png'
import Marea from '@/app/Marea.png'
import { LoginForm } from "@/components/login-form"
import Image from "next/image";
import { ImageZoom } from "@/components/ui/shadcn-io/image-zoom";
import { createContext, useContext, useState } from "react";

export default function LoginPage() {
  const [processedFiles, setProcessedFiles] = useState([]);
  return (
    <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <a href="#" className="flex items-center gap-2 font-medium">
            <Image src={LiaxLogo} width={128} alt="Liax Logo" />
          </a>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
              <LoginForm setFunction={setProcessedFiles} currentState={processedFiles} />
          </div>
        </div>
      </div>
      <div className="relative hidden bg-muted lg:block grid grid-cols-2 gap-2 h-full">
        <div className="bg-blue-500 p-10 h-auto text-gray-800 border-gray-950">
          <p className="font-bold text-2xl">Veja as imagens sendo processadas</p>
        </div>
        <div className=" p-10 h-full  border-gray-950">
          {
            processedFiles.map((processedImages, key) => (
            <div className="grid grid-cols-4 gap-5 mt-5" key={key}>
              {processedImages.map((image, key) => (<div className="mx-auto" key={key}><ImageZoom><Image width={600} height={100} className="rounded-[10px] border-blue-700 border-2" src={"data:image/png;base64," + image} alt="Imagem enviada" /></ImageZoom></div>))}
            </div>))
          }
        </div>
      </div>
    </div>
  );
}
