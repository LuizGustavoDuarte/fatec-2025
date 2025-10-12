'use client'
//import '@/features/gallery/gallery.css'
import PropTypes from 'prop-types'
import Image from 'next/image'

export default function Gallery({ galleries = [], className = '' }) {
  return (
    <section className={`gallery-root ${className}`}>
      {galleries.map((group, gi) => (
        <div className='gallery-row' key={gi}>
          {group.map((image, ii) => {
            const src = image?.startsWith?.('data:') ? image : `data:image/png;base64,${image}`
            return (
              <div className='gallery-item' key={ii}>
                <Image
                  src={src}
                  alt={`Imagem ${gi}-${ii}`}
                  width={240}
                  height={160}
                  className='gallery-image'
                  unoptimized
                />
              </div>
            )
          })}
        </div>
      ))}
    </section>
  )
}

Gallery.propTypes = {
  galleries: PropTypes.arrayOf(PropTypes.array).isRequired,
  className: PropTypes.string,
}
