import { cn, encodeFileToBase64 } from '@/lib/utils'
import { api } from '@/lib/api'
import { Button, Input } from '@/components'
import { usePolling } from '@/hooks'
import { useCallback, useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'

function ImageUploadForm({
  className,
  currentFiles = [],
  onFilesChange = () => {},
  ...props
}) {
  const {
    register,
    handleSubmit,
  } = useForm()

  const [isSubmitting, setIsSubmitting] = useState(false)
  
  const { start: startPolling, stop: stopPolling } = usePolling()

  useEffect(() => {
    return () => {
      stopPolling()
    }
  }, [stopPolling])

  const onSubmit = useCallback(async (data) => {
    if (!data?.image?.[0]) return
    setIsSubmitting(true)
    const file = data.image[0]
    try {
      const base64 = await encodeFileToBase64(file)
      const cid = `${Date.now()}-${Math.random().toString(36).slice(2,8)}`
      onFilesChange((prev) => [...prev, { cid, images: [base64] }])

      const resp = await api.post(`/image/upload/1`, { imageBase64: base64 })
      const id = resp?.data
      if (!id) {
        console.warn('ID não foi encontrado na resposta:', resp)
        setIsSubmitting(false)
        return
      }

      const procResult = await startPolling(`/image/get/${id}/processes`, (procData) => {
        onFilesChange((prev) =>
          prev.map((entry) =>
            entry && entry.cid === cid ? { ...entry, images: [base64, ...procData] } : entry
          )
        )
      })

      if (!procResult) {
        console.warn('Polling terminou sem dados ou atingiu o máximo de tentativas:', id)
      }
    } catch (err) {
      console.error('Ocorreu um erro no upload/processamento:', err)
    } finally {
      setIsSubmitting(false)
    }
  }, [onFilesChange, startPolling])

  return (
    <form className={cn('flex flex-col gap-6', className)} onSubmit={handleSubmit(onSubmit)} {...props}>
      <div className='flex flex-col items-center gap-2 text-center'>
        <h1 className='text-2xl font-bold'>Escolha uma imagem para realizar o processamento</h1>
      </div>
      <div className='grid gap-6'>
        <div className='grid gap-2'>
          <Input
            id='image'
            type='file'
            accept='image/*'
            {...register('image', { required: true })}
          />
        </div>
        <Button type='submit' className='w-full' disabled={isSubmitting}>
          {isSubmitting ? 'Processando...' : 'Processar'}
        </Button>
      </div>
    </form>
  )
}

export { ImageUploadForm }
export default ImageUploadForm
