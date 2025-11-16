import React, { useState, useEffect, useRef } from 'react'
import { useAuth } from '../context/AuthContext'
import apiService from '../services/api'

const LoginPage = ({ isDarkMode, isSystemTheme, toggleDarkMode }) => {
  const { login } = useAuth()
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  })
  
  // Use localStorage to persist error across re-renders
  const [error, setError] = useState(() => {
    const savedError = localStorage.getItem('pharma_login_error')
    return savedError || ''
  })
  
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  
  // Forgot Password Flow State - Initialize from localStorage if available
  const [showForgotPassword, setShowForgotPassword] = useState(false)
  const [forgotPasswordStep, setForgotPasswordStep] = useState(() => {
    const saved = localStorage.getItem('pharma_forgot_password_step')
    return saved ? parseInt(saved, 10) : 1
  })
  const [forgotPasswordData, setForgotPasswordData] = useState(() => {
    const saved = localStorage.getItem('pharma_forgot_password_data')
    return saved ? JSON.parse(saved) : {
      email: '',
      otp: '',
      password: '',
      confirmPassword: ''
    }
  })
  const [forgotPasswordError, setForgotPasswordError] = useState('')
  const [forgotPasswordLoading, setForgotPasswordLoading] = useState(false)
  const [otpResendCooldown, setOtpResendCooldown] = useState(() => {
    const saved = localStorage.getItem('pharma_forgot_password_cooldown')
    const savedTime = saved ? parseInt(saved, 10) : 0
    // Check if cooldown has expired
    if (savedTime > 0) {
      const savedTimestamp = localStorage.getItem('pharma_forgot_password_cooldown_timestamp')
      if (savedTimestamp) {
        const elapsed = Math.floor((Date.now() - parseInt(savedTimestamp, 10)) / 1000)
        return Math.max(0, savedTime - elapsed)
      }
    }
    return 0
  })
  const otpInputRefs = useRef([])
  
  // Persist error to localStorage whenever it changes
  useEffect(() => {
    if (error) {
      localStorage.setItem('pharma_login_error', error)
    } else {
      localStorage.removeItem('pharma_login_error')
    }
  }, [error])
  
  // Clear error when component unmounts
  useEffect(() => {
    return () => {
      localStorage.removeItem('pharma_login_error')
    }
  }, [])
  
  // Function to clear error (when user starts typing)
  const clearError = () => {
    setError('')
    localStorage.removeItem('pharma_login_error')
  }

  // Handle form submission for real login
  const handleSubmit = async (e) => {
    // Prevent default form submission
    e.preventDefault()
    e.stopPropagation()
    
    // Clear any existing error and set submitting state
    clearError()
    setIsSubmitting(true)

    try {
      const result = await login(formData.username, formData.password)
      
      if (!result || !result.success) {
        // Convert technical errors to user-friendly messages
        const errorMessage = result?.error || 'Login failed - no error message received'
        const friendlyError = getFriendlyErrorMessage(errorMessage)
        
        // Set error immediately and persist it
        setError(friendlyError)
        localStorage.setItem('pharma_login_error', friendlyError)
        
        return false
      } else {
        clearError()
      }
    } catch (err) {
      const friendlyError = getFriendlyErrorMessage(err?.message || 'An unexpected error occurred')
      
      // Set error immediately and persist it
      setError(friendlyError)
      localStorage.setItem('pharma_login_error', friendlyError)
      
      return false
    } finally {
      setIsSubmitting(false)
    }
  }

  // Convert technical error messages to user-friendly ones
  const getFriendlyErrorMessage = (error) => {
    if (!error) return 'Something went wrong. Please try again.'
    
    const errorStr = error.toLowerCase()
    
    // Handle prefixed error types from AuthContext
    if (errorStr.startsWith('network_error:')) {
      return '🌐 Unable to connect to the server. Please check if the backend server is running and try again.'
    }
    
    if (errorStr.startsWith('server_error:')) {
      return '🔧 Server is temporarily unavailable. Please try again in a few minutes.'
    }
    
    if (errorStr.startsWith('auth_error:')) {
      return '🔐 Invalid username or password. Please check your credentials and try again.'
    }
    
    // Spring Boot specific errors - check FIRST before generic server errors
    if (errorStr.includes('bad credentials') || errorStr.includes('badcredentialsexception')) {
      return '🔐 Invalid username or password. Please try again.'
    }
    
    // Authentication-related errors - check before server errors
    if (errorStr.includes('unauthorized') || errorStr.includes('401')) {
      return '🔐 Invalid username or password. Please check your credentials and try again.'
    }
    
    if (errorStr.includes('forbidden') || errorStr.includes('403')) {
      return '🚫 Access denied. Please contact your administrator.'
    }
    
    // Login specific errors
    if (errorStr.includes('login failed') || errorStr.includes('authentication failed')) {
      return '🔐 Invalid username or password. Please try again.'
    }
    
    if (errorStr.includes('invalid credentials')) {
      return '🔐 Invalid username or password. Please try again.'
    }
    
    // Network/Connection errors
    if (errorStr.includes('network') || errorStr.includes('fetch') || errorStr.includes('failed to fetch')) {
      return '🌐 Unable to connect to the server. Please check your internet connection and try again.'
    }
    
    // Server errors (check after authentication errors)
    if (errorStr.includes('500') || errorStr.includes('internal server error')) {
      return '🔧 Server is temporarily unavailable. Please try again in a few minutes.'
    }
    
    if (errorStr.includes('503') || errorStr.includes('service unavailable')) {
      return '🔧 Service is temporarily down for maintenance. Please try again later.'
    }
    
    // Validation errors
    if (errorStr.includes('bad request') || errorStr.includes('400')) {
      return '📝 Please check your username and password format.'
    }
    
    // Timeout errors
    if (errorStr.includes('timeout')) {
      return '⏱️ Request took too long. Please check your connection and try again.'
    }
    
    // CORS errors
    if (errorStr.includes('cors') || errorStr.includes('cross-origin')) {
      return '🌐 Connection issue. Please refresh the page and try again.'
    }
    
    // JWT/Token errors
    if (errorStr.includes('jwt') || errorStr.includes('token')) {
      return '🔐 Authentication error. Please try logging in again.'
    }
    

    
    // Handle NullPointerException when user is not found
    if (errorStr.includes('nullpointerexception') || errorStr.includes('cannot invoke') || 
        (errorStr.includes('null') && errorStr.includes('user'))) {
      return '🔐 Invalid username or password. Please try again.'
    }
    
    if (errorStr.includes('user not found')) {
      return '👤 Username not found. Please check your username or contact support.'
    }
    
    if (errorStr.includes('account locked') || errorStr.includes('locked')) {
      return '🔒 Your account has been locked. Please contact support for assistance.'
    }
    
    if (errorStr.includes('account disabled') || errorStr.includes('disabled')) {
      return '🚫 Your account is disabled. Please contact support for assistance.'
    }
    
    // Default fallback for any other error
    return '❌ Unable to sign in. Please check your credentials and try again.'
  }

  // Persist forgot password state to localStorage
  useEffect(() => {
    if (forgotPasswordStep > 1 || forgotPasswordData.email) {
      localStorage.setItem('pharma_forgot_password_step', forgotPasswordStep.toString())
      localStorage.setItem('pharma_forgot_password_data', JSON.stringify(forgotPasswordData))
    }
  }, [forgotPasswordStep, forgotPasswordData])

  // Persist OTP cooldown to localStorage
  useEffect(() => {
    if (otpResendCooldown > 0) {
      localStorage.setItem('pharma_forgot_password_cooldown', otpResendCooldown.toString())
      localStorage.setItem('pharma_forgot_password_cooldown_timestamp', Date.now().toString())
    } else {
      localStorage.removeItem('pharma_forgot_password_cooldown')
      localStorage.removeItem('pharma_forgot_password_cooldown_timestamp')
    }
  }, [otpResendCooldown])

  // OTP Resend Cooldown Timer
  useEffect(() => {
    if (otpResendCooldown > 0) {
      const timer = setTimeout(() => setOtpResendCooldown(otpResendCooldown - 1), 1000)
      return () => clearTimeout(timer)
    }
  }, [otpResendCooldown])

  // Handle Forgot Password - Step 1: Request OTP
  const handleForgotPasswordRequest = async (e) => {
    e.preventDefault()
    setForgotPasswordError('')
    setForgotPasswordLoading(true)

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!forgotPasswordData.email.trim()) {
      setForgotPasswordError('📧 Please enter your email address.')
      setForgotPasswordLoading(false)
      return
    }
    if (!emailRegex.test(forgotPasswordData.email)) {
      setForgotPasswordError('📧 Please enter a valid email address.')
      setForgotPasswordLoading(false)
      return
    }

    try {
      const response = await apiService.auth.forgotPassword(forgotPasswordData.email)
      
      // Check if response indicates success
      if (response && (response === 'OTP sent successfully!' || response.message === 'OTP sent successfully!')) {
        setForgotPasswordStep(2)
        setOtpResendCooldown(60) // 60 second cooldown
        setForgotPasswordError('')
      } else {
        setForgotPasswordError('❌ Failed to send OTP. Please try again.')
      }
    } catch (err) {
      const errorMessage = err?.message || 'Failed to send OTP'
      if (errorMessage.includes('Email not found') || errorMessage.includes('email not found')) {
        setForgotPasswordError('📧 Email not found. Please check your email address.')
      } else if (errorMessage.includes('network') || errorMessage.includes('fetch')) {
        setForgotPasswordError('🌐 Network error. Please check your connection and try again.')
      } else {
        setForgotPasswordError(`❌ ${errorMessage}`)
      }
    } finally {
      setForgotPasswordLoading(false)
    }
  }

  // Handle OTP Verification - Step 2
  const handleOtpVerification = async (e) => {
    e.preventDefault()
    setForgotPasswordError('')
    
    // OTP validation (assuming 6-digit OTP)
    const otp = forgotPasswordData.otp.replace(/\s/g, '')
    if (!otp || otp.length !== 6) {
      setForgotPasswordError('🔢 Please enter a valid 6-digit OTP.')
      return
    }

    setForgotPasswordLoading(true)

    try {
      const response = await apiService.auth.verifyOtp(forgotPasswordData.email, otp)
      
      // Check if OTP is verified
      if (response && (response === 'OTP verified!' || response.message === 'OTP verified!')) {
        setForgotPasswordStep(3)
        setForgotPasswordError('')
      } else {
        setForgotPasswordError('❌ Failed to verify OTP. Please try again.')
      }
    } catch (err) {
      const errorMessage = err?.message || 'Failed to verify OTP'
      if (errorMessage.includes('invalid') || errorMessage.includes('expired')) {
        setForgotPasswordError('🔢 Invalid or expired OTP. Please try again or request a new one.')
      } else if (errorMessage.includes('network') || errorMessage.includes('fetch')) {
        setForgotPasswordError('🌐 Network error. Please check your connection and try again.')
      } else {
        setForgotPasswordError(`❌ ${errorMessage}`)
      }
    } finally {
      setForgotPasswordLoading(false)
    }
  }

  // Handle Password Reset - Step 3
  const handlePasswordReset = async (e) => {
    e.preventDefault()
    setForgotPasswordError('')

    // Password validation
    if (!forgotPasswordData.password) {
      setForgotPasswordError('🔒 Please enter a new password.')
      return
    }
    if (forgotPasswordData.password.length < 8) {
      setForgotPasswordError('🔒 Password must be at least 8 characters long.')
      return
    }
    if (forgotPasswordData.password !== forgotPasswordData.confirmPassword) {
      setForgotPasswordError('🔒 Passwords do not match. Please try again.')
      return
    }

    setForgotPasswordLoading(true)

    try {
      const otp = forgotPasswordData.otp.replace(/\s/g, '')
      const response = await apiService.auth.resetPassword(
        forgotPasswordData.email,
        otp,
        forgotPasswordData.password
      )
      
      // Check if password reset is successful
      if (response && (response === 'Password reset successful' || response.message === 'Password reset successful')) {
        // Success! Close modal and show success message
        setShowForgotPassword(false)
        setForgotPasswordStep(1)
        setForgotPasswordData({ email: '', otp: '', password: '', confirmPassword: '' })
        setForgotPasswordError('')
        setOtpResendCooldown(0)
        // Clear saved state from localStorage on successful reset
        localStorage.removeItem('pharma_forgot_password_step')
        localStorage.removeItem('pharma_forgot_password_data')
        localStorage.removeItem('pharma_forgot_password_cooldown')
        localStorage.removeItem('pharma_forgot_password_cooldown_timestamp')
        // Show success message in login error area temporarily
        setError('✅ Password reset successful! Please log in with your new password.')
        setTimeout(() => setError(''), 5000)
      } else {
        setForgotPasswordError('❌ Failed to reset password. Please try again.')
      }
    } catch (err) {
      const errorMessage = err?.message || 'Failed to reset password'
      if (errorMessage.includes('invalid') || errorMessage.includes('expired')) {
        setForgotPasswordError('🔢 OTP is invalid or expired. Please start over.')
        setForgotPasswordStep(1)
      } else if (errorMessage.includes('network') || errorMessage.includes('fetch')) {
        setForgotPasswordError('🌐 Network error. Please check your connection and try again.')
      } else {
        setForgotPasswordError(`❌ ${errorMessage}`)
      }
    } finally {
      setForgotPasswordLoading(false)
    }
  }

  // Handle OTP input (auto-advance and formatting)
  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return // Only allow digits
    
    const newOtp = forgotPasswordData.otp.split('')
    newOtp[index] = value.slice(-1) // Only take last character
    const otpString = newOtp.join('').slice(0, 6) // Limit to 6 digits
    
    setForgotPasswordData({ ...forgotPasswordData, otp: otpString })
    
    // Auto-advance to next input
    if (value && index < 5) {
      otpInputRefs.current[index + 1]?.focus()
    }
  }

  // Handle OTP paste
  const handleOtpPaste = (e) => {
    e.preventDefault()
    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6)
    if (pastedData.length === 6) {
      setForgotPasswordData({ ...forgotPasswordData, otp: pastedData })
      otpInputRefs.current[5]?.focus()
    }
  }

  // Handle backspace in OTP input
  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !forgotPasswordData.otp[index] && index > 0) {
      otpInputRefs.current[index - 1]?.focus()
    }
  }

  // Resend OTP
  const handleResendOtp = async () => {
    if (otpResendCooldown > 0) return
    
    setForgotPasswordError('')
    setForgotPasswordLoading(true)
    setForgotPasswordData({ ...forgotPasswordData, otp: '' })

    try {
      await apiService.auth.forgotPassword(forgotPasswordData.email)
      setOtpResendCooldown(60)
      setForgotPasswordError('')
      // Clear OTP inputs
      otpInputRefs.current.forEach(ref => ref?.clear?.() || (ref && (ref.value = '')))
    } catch (err) {
      setForgotPasswordError('❌ Failed to resend OTP. Please try again.')
    } finally {
      setForgotPasswordLoading(false)
    }
  }

  // Reset forgot password flow (and clear localStorage)
  const resetForgotPasswordFlow = () => {
    setShowForgotPassword(false)
    setForgotPasswordStep(1)
    setForgotPasswordData({ email: '', otp: '', password: '', confirmPassword: '' })
    setForgotPasswordError('')
    setOtpResendCooldown(0)
    // Clear saved state from localStorage
    localStorage.removeItem('pharma_forgot_password_step')
    localStorage.removeItem('pharma_forgot_password_data')
    localStorage.removeItem('pharma_forgot_password_cooldown')
    localStorage.removeItem('pharma_forgot_password_cooldown_timestamp')
  }

  // Handle modal close - don't reset if user accidentally clicks outside
  const handleModalClose = (e) => {
    // Only close if clicking the backdrop, not the modal content
    if (e.target === e.currentTarget) {
      setShowForgotPassword(false)
      // Don't reset the state - it's saved in localStorage
    }
  }


  return (
    <div className={`min-h-screen flex items-center justify-center transition-colors duration-300 ${
      isDarkMode ? 'bg-gray-900' : 'bg-pharma-light bg-gray-50'
    }`}>
      <div className={`max-w-md w-full mx-4 p-8 rounded-xl shadow-2xl transition-colors duration-300 ${
        isDarkMode ? 'bg-gray-800 border border-gray-700' : 'bg-white border border-gray-200'
      }`}>
        {/* Logo and Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-gradient-to-br from-pharma-teal to-pharma-medium rounded-xl flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
              <path d="M19 8h-2v3h-3v2h3v3h2v-3h3v-2h-3V8zM4 6h5v2h2V6h5v5h2v2h-2v5H11v-2H9v2H4v-5H2v-2h2V6zm5 5H7v2h2v-2z"/>
            </svg>
          </div>
          <h1 className={`text-3xl font-bold transition-colors duration-200 ${
            isDarkMode ? 'text-white' : 'text-gray-900'
          }`}>PharmaTrack</h1>
          <p className={`text-sm mt-2 transition-colors duration-200 ${
            isDarkMode ? 'text-gray-400' : 'text-gray-600'
          }`}>Inventory Management System</p>
        </div>

        {/* Error Message */}
        {error && (
          <div className={`mb-4 p-4 rounded-lg border-l-4 transition-all duration-300 ${
            isDarkMode 
              ? 'bg-red-900/10 border-red-500/50 border-l-red-500' 
              : 'bg-red-50 border-red-200 border-l-red-500'
          }`}>
            <div className="flex items-start">
              <svg className={`w-5 h-5 mt-0.5 mr-3 flex-shrink-0 ${
                isDarkMode ? 'text-red-400' : 'text-red-500'
              }`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd"></path>
              </svg>
              <div>
                <h4 className={`text-sm font-medium ${
                  isDarkMode ? 'text-red-300' : 'text-red-800'
                }`}>
                  Sign In Error
                </h4>
                <p className={`text-sm mt-1 ${
                  isDarkMode ? 'text-red-400' : 'text-red-700'
                }`}>
                  {error}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-700'
            }`}>
              Username
            </label>
            <input
              type="text"
              required
              value={formData.username}
              onChange={(e) => {
                setFormData({ ...formData, username: e.target.value })
                if (error) clearError()
              }}
              className={`w-full px-4 py-3 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                isDarkMode 
                  ? 'bg-gray-700 border-gray-600 text-white placeholder-gray-400 focus:border-pharma-medium' 
                  : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-pharma-medium'
              }`}
              placeholder="Enter your username"
              disabled={isSubmitting}
            />
          </div>
          
          <div>
            <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
              isDarkMode ? 'text-gray-300' : 'text-gray-700'
            }`}>
              Password
            </label>
            <div className="relative">
              <input
                type={showPassword ? "text" : "password"}
                required
                value={formData.password}
                onChange={(e) => {
                  setFormData({ ...formData, password: e.target.value })
                  if (error) clearError()
                }}
                className={`w-full px-4 py-3 pr-12 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                  isDarkMode 
                    ? 'bg-gray-700 border-gray-600 text-white placeholder-gray-400 focus:border-pharma-medium' 
                    : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-pharma-medium'
                }`}
                placeholder="Enter your password"
                disabled={isSubmitting}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className={`absolute inset-y-0 right-0 pr-3 flex items-center transition-colors duration-200 ${
                  isDarkMode 
                    ? 'text-gray-400 hover:text-gray-300' 
                    : 'text-gray-500 hover:text-gray-700'
                }`}
                disabled={isSubmitting}
              >
                {showPassword ? (
                  // Eye Slash Icon (Hide Password)
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"></path>
                  </svg>
                ) : (
                  // Eye Icon (Show Password)
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                  </svg>
                )}
              </button>
            </div>
          </div>

          <div className="flex items-center justify-between">
            <label className="flex items-center">
              <input type="checkbox" className="w-4 h-4 text-pharma-teal bg-gray-100 border-gray-300 rounded focus:ring-pharma-medium focus:ring-2" />
              <span className={`ml-2 text-sm transition-colors duration-200 ${
                isDarkMode ? 'text-gray-300' : 'text-gray-700'
              }`}>Remember me</span>
            </label>
            <button
              type="button"
              onClick={() => {
                // Restore state from localStorage when opening modal
                const savedStep = localStorage.getItem('pharma_forgot_password_step')
                const savedData = localStorage.getItem('pharma_forgot_password_data')
                const savedCooldown = localStorage.getItem('pharma_forgot_password_cooldown')
                const savedCooldownTimestamp = localStorage.getItem('pharma_forgot_password_cooldown_timestamp')
                
                if (savedStep) {
                  setForgotPasswordStep(parseInt(savedStep, 10))
                }
                if (savedData) {
                  setForgotPasswordData(JSON.parse(savedData))
                }
                if (savedCooldown && savedCooldownTimestamp) {
                  const elapsed = Math.floor((Date.now() - parseInt(savedCooldownTimestamp, 10)) / 1000)
                  const remaining = Math.max(0, parseInt(savedCooldown, 10) - elapsed)
                  setOtpResendCooldown(remaining)
                }
                setShowForgotPassword(true)
              }}
              className="text-sm text-pharma-teal hover:text-pharma-medium transition-colors duration-200 focus:outline-none"
            >
              Forgot password?
            </button>
          </div>

          <div className="space-y-3">
             <button
               type="submit"
               disabled={isSubmitting}
               className={`w-full py-3 px-4 rounded-lg font-semibold transition-all duration-200 shadow-lg border-2 ${
                 isSubmitting
                   ? 'bg-gray-400 border-gray-400 cursor-not-allowed text-gray-600'
                   : isDarkMode
                     ? 'bg-gradient-to-r from-pharma-teal to-pharma-medium text-white border-pharma-teal hover:from-pharma-medium hover:to-pharma-teal hover:border-pharma-medium transform hover:scale-105 hover:shadow-xl hover:shadow-pharma-teal/25'
                     : 'bg-gradient-to-r from-pharma-teal to-pharma-medium text-white border-pharma-teal hover:from-pharma-medium hover:to-pharma-teal hover:border-pharma-medium transform hover:scale-105 hover:shadow-xl hover:shadow-pharma-teal/25'
               }`}
             >
              {isSubmitting ? (
                <div className="flex items-center justify-center">
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  Signing In...
                </div>
              ) : (
                'Sign In'
              )}
            </button>

          </div>
        </form>


        {/* Forgot Password Modal */}
        {showForgotPassword && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={handleModalClose}>
            <div 
              className={`max-w-md w-full rounded-xl shadow-2xl transition-colors duration-300 ${
                isDarkMode ? 'bg-gray-800 border border-gray-700' : 'bg-white border border-gray-200'
              }`}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="p-6">
                {/* Header */}
                <div className="flex items-center justify-between mb-6">
                  <h2 className={`text-2xl font-bold transition-colors duration-200 ${
                    isDarkMode ? 'text-white' : 'text-gray-900'
                  }`}>
                    {forgotPasswordStep === 1 && 'Reset Password'}
                    {forgotPasswordStep === 2 && 'Enter OTP'}
                    {forgotPasswordStep === 3 && 'New Password'}
                  </h2>
                  <button
                    onClick={resetForgotPasswordFlow}
                    className={`p-1 rounded-lg transition-colors duration-200 ${
                      isDarkMode 
                        ? 'text-gray-400 hover:text-gray-300 hover:bg-gray-700' 
                        : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>

                {/* Progress Steps */}
                <div className="flex items-center mb-6 w-full">
                  {[1, 2, 3].map((step, index) => {
                    const isCompleted = forgotPasswordStep > step
                    const isCurrent = forgotPasswordStep === step
                    const isPending = forgotPasswordStep < step
                    
                    return (
                      <React.Fragment key={step}>
                        <div className="flex items-center flex-shrink-0 relative z-10">
                          <div className={`flex items-center justify-center w-8 h-8 rounded-full border-2 transition-colors duration-200 ${
                            forgotPasswordStep >= step
                              ? 'bg-pharma-teal border-pharma-teal text-white'
                              : isDarkMode
                                ? 'border-gray-600 text-gray-500'
                                : 'border-gray-300 text-gray-400'
                          }`}>
                            {isCompleted ? (
                              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                              </svg>
                            ) : (
                              <span className="text-sm font-semibold">{step}</span>
                            )}
                          </div>
                        </div>
                        {index < 2 && (
                          <div 
                            className={`h-1 mx-2 transition-colors duration-200 ${
                              isCompleted
                                ? 'bg-pharma-teal'
                                : isDarkMode
                                  ? 'bg-gray-700'
                                  : 'bg-gray-200'
                            }`} 
                            style={{ 
                              flex: '1 1 auto',
                              minWidth: '30px',
                              maxWidth: 'none'
                            }} 
                          />
                        )}
                      </React.Fragment>
                    )
                  })}
                </div>

                {/* Error Message */}
                {forgotPasswordError && (
                  <div className={`mb-4 p-3 rounded-lg border-l-4 transition-all duration-300 ${
                    isDarkMode 
                      ? 'bg-red-900/10 border-red-500/50 border-l-red-500' 
                      : 'bg-red-50 border-red-200 border-l-red-500'
                  }`}>
                    <p className={`text-sm ${
                      isDarkMode ? 'text-red-400' : 'text-red-700'
                    }`}>
                      {forgotPasswordError}
                    </p>
                  </div>
                )}

                {/* Step 1: Email Input */}
                {forgotPasswordStep === 1 && (
                  <form onSubmit={handleForgotPasswordRequest} className="space-y-4">
                    <div>
                      <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
                        isDarkMode ? 'text-gray-300' : 'text-gray-700'
                      }`}>
                        Email Address
                      </label>
                      <input
                        type="email"
                        required
                        value={forgotPasswordData.email}
                        onChange={(e) => {
                          setForgotPasswordData({ ...forgotPasswordData, email: e.target.value })
                          setForgotPasswordError('')
                        }}
                        className={`w-full px-4 py-3 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                          isDarkMode 
                            ? 'bg-gray-700 border-gray-600 text-white placeholder-gray-400 focus:border-pharma-medium' 
                            : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-pharma-medium'
                        }`}
                        placeholder="Enter your email address"
                        disabled={forgotPasswordLoading}
                        autoFocus
                      />
                    </div>
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={resetForgotPasswordFlow}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 border-2 ${
                          isDarkMode
                            ? 'bg-gray-700 border-gray-600 text-gray-300 hover:bg-gray-600'
                            : 'bg-gray-100 border-gray-300 text-gray-700 hover:bg-gray-200'
                        }`}
                        disabled={forgotPasswordLoading}
                      >
                        Cancel
                      </button>
                      <button
                        type="submit"
                        disabled={forgotPasswordLoading}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 shadow-lg border-2 ${
                          forgotPasswordLoading
                            ? 'bg-gray-400 border-gray-400 cursor-not-allowed text-gray-600'
                            : 'bg-gradient-to-r from-pharma-teal to-pharma-medium text-white border-pharma-teal hover:from-pharma-medium hover:to-pharma-teal'
                        }`}
                      >
                        {forgotPasswordLoading ? (
                          <div className="flex items-center justify-center">
                            <svg className="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Sending...
                          </div>
                        ) : (
                          'Send OTP'
                        )}
                      </button>
                    </div>
                  </form>
                )}

                {/* Step 2: OTP Input */}
                {forgotPasswordStep === 2 && (
                  <form onSubmit={handleOtpVerification} className="space-y-4">
                    <div>
                      <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
                        isDarkMode ? 'text-gray-300' : 'text-gray-700'
                      }`}>
                        Enter 6-Digit OTP
                      </label>
                      <p className={`text-xs mb-3 transition-colors duration-200 ${
                        isDarkMode ? 'text-gray-400' : 'text-gray-600'
                      }`}>
                        We've sent a verification code to <strong>{forgotPasswordData.email}</strong>
                      </p>
                      <div className="flex gap-2 justify-center">
                        {[0, 1, 2, 3, 4, 5].map((index) => (
                          <input
                            key={index}
                            ref={(el) => (otpInputRefs.current[index] = el)}
                            type="text"
                            inputMode="numeric"
                            maxLength="1"
                            value={forgotPasswordData.otp[index] || ''}
                            onChange={(e) => handleOtpChange(index, e.target.value)}
                            onPaste={handleOtpPaste}
                            onKeyDown={(e) => handleOtpKeyDown(index, e)}
                            className={`w-12 h-12 text-center text-lg font-semibold rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                              isDarkMode 
                                ? 'bg-gray-700 border-gray-600 text-white focus:border-pharma-medium' 
                                : 'bg-white border-gray-300 text-gray-900 focus:border-pharma-medium'
                            }`}
                            disabled={forgotPasswordLoading}
                            autoFocus={index === 0}
                          />
                        ))}
                      </div>
                      <div className="mt-3 text-center">
                        <button
                          type="button"
                          onClick={handleResendOtp}
                          disabled={otpResendCooldown > 0 || forgotPasswordLoading}
                          className={`text-sm transition-colors duration-200 ${
                            otpResendCooldown > 0 || forgotPasswordLoading
                              ? isDarkMode ? 'text-gray-600 cursor-not-allowed' : 'text-gray-400 cursor-not-allowed'
                              : 'text-pharma-teal hover:text-pharma-medium'
                          }`}
                        >
                          {otpResendCooldown > 0 
                            ? `Resend OTP in ${otpResendCooldown}s`
                            : 'Resend OTP'
                          }
                        </button>
                      </div>
                    </div>
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={() => {
                          setForgotPasswordStep(1)
                          setForgotPasswordData({ ...forgotPasswordData, otp: '' })
                          setForgotPasswordError('')
                        }}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 border-2 ${
                          isDarkMode
                            ? 'bg-gray-700 border-gray-600 text-gray-300 hover:bg-gray-600'
                            : 'bg-gray-100 border-gray-300 text-gray-700 hover:bg-gray-200'
                        }`}
                        disabled={forgotPasswordLoading}
                      >
                        Back
                      </button>
                      <button
                        type="submit"
                        disabled={forgotPasswordLoading || forgotPasswordData.otp.length !== 6}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 shadow-lg border-2 ${
                          forgotPasswordLoading || forgotPasswordData.otp.length !== 6
                            ? 'bg-gray-400 border-gray-400 cursor-not-allowed text-gray-600'
                            : 'bg-gradient-to-r from-pharma-teal to-pharma-medium text-white border-pharma-teal hover:from-pharma-medium hover:to-pharma-teal'
                        }`}
                      >
                        {forgotPasswordLoading ? (
                          <div className="flex items-center justify-center">
                            <svg className="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Verifying...
                          </div>
                        ) : (
                          'Verify OTP'
                        )}
                      </button>
                    </div>
                  </form>
                )}

                {/* Step 3: Reset Password */}
                {forgotPasswordStep === 3 && (
                  <form onSubmit={handlePasswordReset} className="space-y-4">
                    <div>
                      <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
                        isDarkMode ? 'text-gray-300' : 'text-gray-700'
                      }`}>
                        New Password
                      </label>
                      <input
                        type="password"
                        required
                        value={forgotPasswordData.password}
                        onChange={(e) => {
                          setForgotPasswordData({ ...forgotPasswordData, password: e.target.value })
                          setForgotPasswordError('')
                        }}
                        className={`w-full px-4 py-3 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                          isDarkMode 
                            ? 'bg-gray-700 border-gray-600 text-white placeholder-gray-400 focus:border-pharma-medium' 
                            : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-pharma-medium'
                        }`}
                        placeholder="Enter new password (min. 8 characters)"
                        disabled={forgotPasswordLoading}
                        autoFocus
                        minLength={8}
                      />
                    </div>
                    <div>
                      <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
                        isDarkMode ? 'text-gray-300' : 'text-gray-700'
                      }`}>
                        Confirm Password
                      </label>
                      <input
                        type="password"
                        required
                        value={forgotPasswordData.confirmPassword}
                        onChange={(e) => {
                          setForgotPasswordData({ ...forgotPasswordData, confirmPassword: e.target.value })
                          setForgotPasswordError('')
                        }}
                        className={`w-full px-4 py-3 rounded-lg border transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-pharma-medium ${
                          isDarkMode 
                            ? 'bg-gray-700 border-gray-600 text-white placeholder-gray-400 focus:border-pharma-medium' 
                            : 'bg-white border-gray-300 text-gray-900 placeholder-gray-500 focus:border-pharma-medium'
                        }`}
                        placeholder="Confirm new password"
                        disabled={forgotPasswordLoading}
                        minLength={8}
                      />
                    </div>
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={() => {
                          setForgotPasswordStep(2)
                          setForgotPasswordData({ ...forgotPasswordData, password: '', confirmPassword: '' })
                          setForgotPasswordError('')
                        }}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 border-2 ${
                          isDarkMode
                            ? 'bg-gray-700 border-gray-600 text-gray-300 hover:bg-gray-600'
                            : 'bg-gray-100 border-gray-300 text-gray-700 hover:bg-gray-200'
                        }`}
                        disabled={forgotPasswordLoading}
                      >
                        Back
                      </button>
                      <button
                        type="submit"
                        disabled={forgotPasswordLoading || !forgotPasswordData.password || !forgotPasswordData.confirmPassword}
                        className={`flex-1 py-3 px-4 rounded-lg font-semibold transition-all duration-200 shadow-lg border-2 ${
                          forgotPasswordLoading || !forgotPasswordData.password || !forgotPasswordData.confirmPassword
                            ? 'bg-gray-400 border-gray-400 cursor-not-allowed text-gray-600'
                            : 'bg-gradient-to-r from-pharma-teal to-pharma-medium text-white border-pharma-teal hover:from-pharma-medium hover:to-pharma-teal'
                        }`}
                      >
                        {forgotPasswordLoading ? (
                          <div className="flex items-center justify-center">
                            <svg className="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Resetting...
                          </div>
                        ) : (
                          'Reset Password'
                        )}
                      </button>
                    </div>
                  </form>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Theme Toggle for Login Page */}
        <div className="mt-6 flex justify-center">
          <button
            onClick={toggleDarkMode}
            className={`p-2 rounded-lg transition-colors duration-200 relative ${
              isDarkMode 
                ? 'bg-gray-700 text-yellow-400 hover:bg-gray-600' 
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
            title={
              isSystemTheme 
                ? `Following system theme (${isDarkMode ? 'Dark' : 'Light'}). Click to override.`
                : isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'
            }
          >
            {isDarkMode ? (
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 2a1 1 0 011 1v1a1 1 0 11-2 0V3a1 1 0 011-1zm4 8a4 4 0 11-8 0 4 4 0 018 0zm-.464 4.95l.707.707a1 1 0 001.414-1.414l-.707-.707a1 1 0 00-1.414 1.414zm2.12-10.607a1 1 0 010 1.414l-.706.707a1 1 0 11-1.414-1.414l.707-.707a1 1 0 011.414 0zM17 11a1 1 0 100-2h-1a1 1 0 100 2h1zm-7 4a1 1 0 011 1v1a1 1 0 11-2 0v-1a1 1 0 011-1zM5.05 6.464A1 1 0 106.465 5.05l-.708-.707a1 1 0 00-1.414 1.414l.707.707zm1.414 8.486l-.707.707a1 1 0 01-1.414-1.414l.707-.707a1 1 0 011.414 1.414zM4 11a1 1 0 100-2H3a1 1 0 000 2h1z" clipRule="evenodd"></path>
              </svg>
            ) : (
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M17.293 13.293A8 8 0 016.707 2.707a8.001 8.001 0 1010.586 10.586z"></path>
              </svg>
            )}
            {isSystemTheme && (
              <div className="absolute -bottom-1 -right-1 w-3 h-3 bg-blue-500 rounded-full border-2 border-white"></div>
            )}
          </button>
        </div>
      </div>
    </div>
  )
}

export default LoginPage
