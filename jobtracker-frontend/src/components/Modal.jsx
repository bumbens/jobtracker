import { createPortal } from 'react-dom'

function UpdateApplication({isOpen, children}) {
    if (!isOpen) return null
    return createPortal(
        <div className='fixed inset-0 z-1000 flex items-center justify-center bg-black/50'>
            <div className='flex flex-col items-center rounded-lg bg-white p-6 w-full max-w-md mx-4 dark:bg-[#1a1a2e] dark:text-[#c3cfe2]' onClick={e => e.stopPropagation()}>
                {children}
            </div>
        </div>,
        document.body
    )
}

export default UpdateApplication