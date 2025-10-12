import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs) {
  return twMerge(clsx(...inputs))
}

export function encodeFileToBase64(file) {
  return new Promise((resolve, reject) => {
    if (!file) return reject(new Error('Nenhuma imagem fornecida'))
    const reader = new FileReader()
    reader.onload = () => {
      const result = reader.result
      if (typeof result === 'string') {
        const commaIndex = result.indexOf(',')
        resolve(commaIndex >= 0 ? result.slice(commaIndex + 1) : result)
      } else {
        reject(new Error('Erro ao converter imagem em base64'))
      }
    }
    reader.onerror = (err) => reject(err)
    reader.readAsDataURL(file)
  })
}
