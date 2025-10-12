'use client'
import LiaxLogo from '@public/images/LiaxLogo.png'
import { ImageUploadForm, ImageZoom } from '@/components'
import { useMemo, useState } from 'react'
import Image from 'next/image'

export default function InitialPage() {
  const [processedFiles, setProcessedFiles] = useState([])

  const galleries = useMemo(() => processedFiles, [processedFiles])

  return (
    <div className='grid min-h-svh lg:grid-cols-2'>
      <div className='flex flex-col gap-4 p-6 md:p-10'>
        <div className='flex justify-center gap-2 md:justify-start'>
          <a href='#' className='flex items-center gap-2 font-medium'>
            <Image src={LiaxLogo} width={128} alt='Liax Logo' />
          </a>
        </div>
        <div className='flex flex-1 items-center justify-center'>
          <div className='w-full max-w-xs'>
            <ImageUploadForm
              currentFiles={processedFiles}
              onFilesChange={setProcessedFiles}
            />
          </div>
        </div>
      </div>
      <div className='relative hidden bg-muted lg:block grid grid-cols-2 gap-2 h-full'>
        <div className='bg-blue-500 p-10 h-auto text-gray-800 border-gray-950'>
          <p className='font-bold text-2xl'>Veja as imagens sendo processadas</p>
        </div>
        <div className='p-10 h-full border-gray-950 overflow-auto'>
          {galleries.map((entry, idx) => (
            <div className='grid grid-cols-4 gap-5 mt-5' key={entry?.cid ?? idx}>
              {(entry?.images || []).map((image, k) => {
                const src = image?.startsWith?.('data:') ? image : `data:image/png;base64,${image}`
                return (
                  <div className='mx-auto' key={k}>
                    <ImageZoom>
                      <Image
                        width={240}
                        height={160}
                        className='rounded-[10px] border-blue-700 border-2 object-cover'
                        src={src}
                        alt={`Imagem enviada ${idx}-${k}`}
                        unoptimized
                      />
                    </ImageZoom>
                  </div>
                )
              })}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
