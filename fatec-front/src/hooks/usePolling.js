import { api } from '@/lib/api'
import { useRef, useEffect, useCallback } from 'react'

export default function usePolling() {
  const controllerRef = useRef(null)

  useEffect(() => {
    return () => {
      if (controllerRef.current) {
        controllerRef.current.abort()
        controllerRef.current = null
      }
    };
  }, [])

  const start = useCallback(
    async (path, onData, { maxAttempts = 20, initialDelay = 1000 } = {}) => {
      if (!path || typeof onData !== 'function') return
      if (controllerRef.current) {
        controllerRef.current.abort()
      }
      const controller = new AbortController()
      controllerRef.current = controller

      let attempt = 0
      let delay = initialDelay

      while (attempt < maxAttempts) {
        try {
          const resp = await api.get(path, { signal: controller.signal })
          const data = resp?.data
          if (data && (Array.isArray(data) ? data.length > 0 : true)) {
            onData(data)
            controllerRef.current = null
            return data
          }
        } catch (err) {
          if (err?.name === 'CanceledError' || err?.message === 'canceled') {
            return
          }
          console.warn('usePolling: erro na requisição', err)
        }

        await new Promise((res) => setTimeout(res, delay))
        attempt += 1
        delay = Math.min(8000, Math.floor(delay * 1.5))
      }

      console.warn('usePolling: interrompido após o máximo de tentativas', path)
      controllerRef.current = null
    },
    []
  )

  const stop = useCallback(() => {
    if (controllerRef.current) {
      controllerRef.current.abort()
      controllerRef.current = null
    }
  }, [])

  return { start, stop }
}
