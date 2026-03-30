import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'shared';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subscription, map } from 'rxjs';
import { AuthService } from 'shared';
import { LoginModalComponent } from '../login-modal/login-modal.component';
import { OpenAccountModalComponent } from '../open-account-modal/open-account-modal.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, SharedModule, LoginModalComponent, OpenAccountModalComponent, RouterModule],
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  showLoginModal = false;
  loginModalInitialView: 'login' | 'forgot-pass-step1' | 'forgot-pass-step2' = 'login';
  showAccountModal = false;
  selectedLanguage = 'English';
  private querySub!: Subscription;

  isLoggedIn$ = this.authService.authState$;
  userRole$ = this.authService.authState$.pipe(map(() => this.authService.getUserRole()));

  constructor() {}

  ngOnInit() {
    this.querySub = this.route.queryParams.subscribe(params => {
      if (params['reset'] === 'true') {
        this.loginModalInitialView = 'forgot-pass-step1';
        this.showLoginModal = true;
        // Clean up URL so it doesn't reopen if they refresh after closing
        this.router.navigate([], { queryParams: { reset: null }, queryParamsHandling: 'merge' });
      }
    });
  }

  ngOnDestroy() {
    if (this.querySub) {
      this.querySub.unsubscribe();
    }
  }

  translations = {
    English: {
      aboutUs: 'About Us',
      contactUs: 'Contact Us',
      help: 'Help',
      bankName: 'SouthernWave Bank',
      tagline: 'Experience Modern Banking',
      login: 'Login',
      openAccount: 'Open an Account',
      totalBalance: 'Total Balance',
      send: 'Send',
      receive: 'Receive',
      pay: 'Pay',
      securityTitle: 'Bank-Level Security',
      securitySubtitle: 'Your money and data are protected with industry-leading security measures',
      benefits: [
        { icon: '💰', title: 'Zero Hidden Fees', description: 'Transparent pricing with no surprise charges' },
        { icon: '⚡', title: '24/7 Support', description: 'Round-the-clock customer assistance' },
        { icon: '🚀', title: 'Fast Onboarding', description: 'Get started in just 5 minutes' },
        { icon: '📊', title: 'Smart Insights', description: 'AI-powered spending analytics' }
      ],
      howItWorksTitle: 'How It Works',
      howItWorksSteps: [
        { step: '1', title: 'Create Account', description: 'Sign up with your mobile number' },
        { step: '2', title: 'Complete KYC', description: 'Verify your identity securely' },
        { step: '3', title: 'Add Money', description: 'Fund your account instantly' },
        { step: '4', title: 'Start Banking', description: 'Enjoy seamless banking experience' }
      ],
      servicesTitle: 'Digital Banking Services',
      services: [
        { icon: '🏦', title: 'Savings Account', description: 'High-interest savings with zero balance' },
        { icon: '💳', title: 'Debit Cards', description: 'Contactless payments worldwide' },
        { icon: '🏠', title: 'Home Loans', description: 'Competitive rates and quick approval' },
        { icon: '🛡️', title: 'Insurance', description: 'Comprehensive coverage plans' },
        { icon: '📈', title: 'Investments', description: 'Mutual funds and SIP options' },
        { icon: '📱', title: 'UPI Payments', description: 'Instant money transfers' }
      ],
      trustTitle: 'Trusted by Millions',
      trustSubtitle: 'Join over 2 million customers who trust SouthernWave Bank',
      testimonials: [
        { name: 'Sarah Johnson', text: 'Best banking app I\'ve ever used. Simple and secure!', rating: 5 },
        { name: 'Mike Chen', text: 'Lightning-fast transfers and excellent customer service.', rating: 5 },
        { name: 'Priya Sharma', text: 'Finally, a bank that understands modern banking needs.', rating: 5 }
      ],
      faqTitle: 'Frequently Asked Questions',
      faqs: [
        { question: 'Is my data secure?', answer: 'Yes, we use bank-level encryption and comply with all regulatory standards.' },
        { question: 'How long does KYC take?', answer: 'KYC verification typically takes 2-3 business days.' },
        { question: 'Are there any hidden fees?', answer: 'No, we believe in transparent pricing with no hidden charges.' },
        { question: 'How do I reset my password?', answer: 'You can reset your password through the app or website using OTP verification.' }
      ],
      finalCtaTitle: 'Ready to Experience Modern Banking?',
      finalCtaSubtitle: 'Join millions who trust SouthernWave Bank for their daily banking needs',
      getStarted: 'Get Started Now',
      copyright: 'All rights reserved.',
      termsConditions: 'Terms & Conditions',
      privacyPolicy: 'Privacy Policy',
      features: [
        {
          icon: '🔒',
          title: 'Secure Online Banking',
          description: 'Your money and data are protected with bank-level security and encryption.'
        },
        {
          icon: '🏦',
          title: 'Manage All Accounts',
          description: 'View and manage all your accounts in one convenient place.'
        },
        {
          icon: '⚡',
          title: 'Fast Transfers',
          description: 'Send money instantly with our lightning-fast transfer system.'
        },
        {
          icon: '🔔',
          title: 'Smart Notifications',
          description: 'Stay informed with intelligent alerts and notifications.'
        },
        {
          icon: '🕐',
          title: '24/7 Support',
          description: 'Get help whenever you need it with our round-the-clock customer support.'
        }
      ],
      modals: {
        openAccountTitle: 'Open an Account',
        openAccountWelcome: 'Welcome to SouthernWave Bank!',
        inBranchExclusivity: 'In-Branch Exclusivity',
        inBranchDesc: 'To ensure the highest level of security and personalized service, SouthernWave Bank currently opens new accounts exclusively at our physical branch locations.',
        visitBranch: 'Visit Our Flagship Branch',
        hqAddressPart1: 'SouthernWave Corporate Center',
        hqAddressPart2: '123 Financial District, Suite 500',
        hqAddressPart3: 'New York, NY 10004',
        hqHours: 'Hours: Mon - Fri, 9:00 AM - 5:00 PM',
        hqContact: 'Contact: 1-800-555-WAVE',
        gotItThanks: 'Got it, thanks!',
        welcomeBack: 'Welcome Back',
        loginSubtitle: 'Secure access to your SouthernWave account',
        emailLabel: 'Email Address',
        passwordLabel: 'Password',
        rememberMe: 'Remember me',
        forgotPasswordLink: 'Forgot Password?',
        loginBtn: 'Login',
        loggingIn: 'Logging in...',
        secureEncrypted: 'Secure encrypted connection',
        resetPasswordTitle: 'Reset Password',
        forgotPasswordSubtitle: 'Enter your details to receive an OTP',
        fullNameLabel: 'Full Name',
        sendOtpBtn: 'Send OTP',
        sending: 'Sending...',
        backToLogin: 'Back to Login',
        enterOtpTitle: 'Enter OTP',
        enterOtpSubtitle: "We've sent a code to your email",
        otpLabel: '6-Digit OTP',
        newPasswordLabel: 'New Password',
        verifyResetBtn: 'Verify & Reset',
        verifying: 'Verifying...'
      }
    },
    Spanish: {
      aboutUs: 'Acerca de',
      contactUs: 'Contáctanos',
      help: 'Ayuda',
      bankName: 'Banco SouthernWave',
      tagline: 'Experimenta la Banca Moderna',
      login: 'Iniciar Sesión',
      openAccount: 'Abrir una Cuenta',
      totalBalance: 'Saldo Total',
      send: 'Enviar',
      receive: 'Recibir',
      pay: 'Pagar',
      securityTitle: 'Seguridad de Nivel Bancario',
      securitySubtitle: 'Tu dinero y datos están protegidos con medidas de seguridad líderes en la industria',
      benefits: [
        { icon: '💰', title: 'Sin Tarifas Ocultas', description: 'Precios transparentes sin cargos sorpresa' },
        { icon: '⚡', title: 'Soporte 24/7', description: 'Asistencia al cliente las 24 horas' },
        { icon: '🚀', title: 'Registro Rápido', description: 'Comienza en solo 5 minutos' },
        { icon: '📊', title: 'Insights Inteligentes', description: 'Análisis de gastos con IA' }
      ],
      howItWorksTitle: 'Cómo Funciona',
      howItWorksSteps: [
        { step: '1', title: 'Crear Cuenta', description: 'Regístrate con tu número móvil' },
        { step: '2', title: 'Completar KYC', description: 'Verifica tu identidad de forma segura' },
        { step: '3', title: 'Agregar Dinero', description: 'Fondea tu cuenta al instante' },
        { step: '4', title: 'Comenzar Banca', description: 'Disfruta una experiencia bancaria perfecta' }
      ],
      servicesTitle: 'Servicios de Banca Digital',
      services: [
        { icon: '🏦', title: 'Cuenta de Ahorros', description: 'Ahorros de alto interés con saldo cero' },
        { icon: '💳', title: 'Tarjetas de Débito', description: 'Pagos sin contacto en todo el mundo' },
        { icon: '🏠', title: 'Préstamos Hipotecarios', description: 'Tasas competitivas y aprobación rápida' },
        { icon: '🛡️', title: 'Seguros', description: 'Planes de cobertura integral' },
        { icon: '📈', title: 'Inversiones', description: 'Fondos mutuos y opciones SIP' },
        { icon: '📱', title: 'Pagos UPI', description: 'Transferencias instantáneas de dinero' }
      ],
      trustTitle: 'Confiado por Millones',
      trustSubtitle: 'Únete a más de 2 millones de clientes que confían en Banco SouthernWave',
      testimonials: [
        { name: 'Sarah Johnson', text: '¡La mejor app bancaria que he usado. Simple y segura!', rating: 5 },
        { name: 'Mike Chen', text: 'Transferencias ultrarrápidas y excelente servicio al cliente.', rating: 5 },
        { name: 'Priya Sharma', text: 'Finalmente, un banco que entiende las necesidades bancarias modernas.', rating: 5 }
      ],
      faqTitle: 'Preguntas Frecuentes',
      faqs: [
        { question: '¿Están seguros mis datos?', answer: 'Sí, usamos encriptación de nivel bancario y cumplimos con todos los estándares regulatorios.' },
        { question: '¿Cuánto tarda el KYC?', answer: 'La verificación KYC generalmente toma 2-3 días hábiles.' },
        { question: '¿Hay tarifas ocultas?', answer: 'No, creemos en precios transparentes sin cargos ocultos.' },
        { question: '¿Cómo restablezco mi contraseña?', answer: 'Puedes restablecer tu contraseña a través de la app o sitio web usando verificación OTP.' }
      ],
      finalCtaTitle: '¿Listo para Experimentar la Banca Moderna?',
      finalCtaSubtitle: 'Únete a millones que confían en Banco SouthernWave para sus necesidades bancarias diarias',
      getStarted: 'Comenzar Ahora',
      copyright: 'Todos los derechos reservados.',
      termsConditions: 'Términos y Condiciones',
      privacyPolicy: 'Política de Privacidad',
      features: [
        {
          icon: '🔒',
          title: 'Banca en Línea Segura',
          description: 'Tu dinero y datos están protegidos con seguridad y encriptación de nivel bancario.'
        },
        {
          icon: '🏦',
          title: 'Gestionar Todas las Cuentas',
          description: 'Ve y gestiona todas tus cuentas en un lugar conveniente.'
        },
        {
          icon: '⚡',
          title: 'Transferencias Rápidas',
          description: 'Envía dinero al instante con nuestro sistema de transferencia ultrarrápido.'
        },
        {
          icon: '🔔',
          title: 'Notificaciones Inteligentes',
          description: 'Mantente informado con alertas y notificaciones inteligentes.'
        },
        {
          icon: '🕐',
          title: 'Soporte 24/7',
          description: 'Obtén ayuda cuando la necesites con nuestro soporte al cliente las 24 horas.'
        }
      ],
      modals: {
        openAccountTitle: 'Abrir una Cuenta',
        openAccountWelcome: '¡Bienvenido a SouthernWave Bank!',
        inBranchExclusivity: 'Exclusividad en Sucursal',
        inBranchDesc: 'Para garantizar el más alto nivel de seguridad y servicio personalizado, actualmente abrimos nuevas cuentas exclusivamente en nuestras sucursales físicas.',
        visitBranch: 'Visite Nuestra Sucursal Principal',
        hqAddressPart1: 'Centro Corporativo SouthernWave',
        hqAddressPart2: '123 Financial District, Suite 500',
        hqAddressPart3: 'Nueva York, NY 10004',
        hqHours: 'Horario: Lun - Vie, 9:00 AM - 5:00 PM',
        hqContact: 'Contacto: 1-800-555-WAVE',
        gotItThanks: '¡Entendido, gracias!',
        welcomeBack: 'Bienvenido de Nuevo',
        loginSubtitle: 'Acceso seguro a su cuenta SouthernWave',
        emailLabel: 'Correo Electrónico',
        passwordLabel: 'Contraseña',
        rememberMe: 'Recuérdame',
        forgotPasswordLink: '¿Olvidó su Contraseña?',
        loginBtn: 'Iniciar Sesión',
        loggingIn: 'Iniciando sesión...',
        secureEncrypted: 'Conexión cifrada segura',
        resetPasswordTitle: 'Restablecer Contraseña',
        forgotPasswordSubtitle: 'Ingrese sus datos para recibir un OTP',
        fullNameLabel: 'Nombre Completo',
        sendOtpBtn: 'Enviar OTP',
        sending: 'Enviando...',
        backToLogin: 'Volver a Iniciar Sesión',
        enterOtpTitle: 'Ingrese OTP',
        enterOtpSubtitle: 'Hemos enviado un código a su correo',
        otpLabel: 'OTP de 6 dígitos',
        newPasswordLabel: 'Nueva Contraseña',
        verifyResetBtn: 'Verificar y Restablecer',
        verifying: 'Verificando...'
      }
    },
    French: {
      aboutUs: 'À Propos',
      contactUs: 'Contactez-nous',
      help: 'Aide',
      bankName: 'Banque SouthernWave',
      tagline: 'Découvrez la Banque Moderne',
      login: 'Connexion',
      openAccount: 'Ouvrir un Compte',
      totalBalance: 'Solde Total',
      send: 'Envoyer',
      receive: 'Recevoir',
      pay: 'Payer',
      securityTitle: 'Sécurité de Niveau Bancaire',
      securitySubtitle: 'Votre argent et vos données sont protégés par des mesures de sécurité de pointe',
      benefits: [
        { icon: '💰', title: 'Zéro Frais Cachés', description: 'Tarification transparente sans frais surprises' },
        { icon: '⚡', title: 'Support 24/7', description: 'Assistance client 24h/24' },
        { icon: '🚀', title: 'Intégration Rapide', description: 'Commencez en seulement 5 minutes' },
        { icon: '📊', title: 'Insights Intelligents', description: 'Analyses de dépenses pilotées par IA' }
      ],
      howItWorksTitle: 'Comment Ça Marche',
      howItWorksSteps: [
        { step: '1', title: 'Créer un Compte', description: 'Inscrivez-vous avec votre numéro mobile' },
        { step: '2', title: 'Compléter KYC', description: 'Vérifiez votre identité en toute sécurité' },
        { step: '3', title: 'Ajouter de l\'Argent', description: 'Alimentez votre compte instantanément' },
        { step: '4', title: 'Commencer la Banque', description: 'Profitez d\'une expérience bancaire fluide' }
      ],
      servicesTitle: 'Services Bancaires Numériques',
      services: [
        { icon: '🏦', title: 'Compte d\'Epargne', description: 'Épargne à haut intérêt avec solde zéro' },
        { icon: '💳', title: 'Cartes de Débit', description: 'Paiements sans contact dans le monde entier' },
        { icon: '🏠', title: 'Prêts Immobiliers', description: 'Taux compétitifs et approbation rapide' },
        { icon: '🛡️', title: 'Assurance', description: 'Plans de couverture complète' },
        { icon: '📈', title: 'Investissements', description: 'Fonds communs et options SIP' },
        { icon: '📱', title: 'Paiements UPI', description: 'Transferts d\'argent instantanés' }
      ],
      trustTitle: 'Fait Confiance par des Millions',
      trustSubtitle: 'Rejoignez plus de 2 millions de clients qui font confiance à Banque SouthernWave',
      testimonials: [
        { name: 'Sarah Johnson', text: 'Meilleure app bancaire que j\'aie jamais utilisée. Simple et sécurisée!', rating: 5 },
        { name: 'Mike Chen', text: 'Transferts ultra-rapides et excellent service client.', rating: 5 },
        { name: 'Priya Sharma', text: 'Enfin, une banque qui comprend les besoins bancaires modernes.', rating: 5 }
      ],
      faqTitle: 'Questions Fréquemment Posées',
      faqs: [
        { question: 'Mes données sont-elles sécurisées?', answer: 'Oui, nous utilisons un cryptage de niveau bancaire et respectons toutes les normes réglementaires.' },
        { question: 'Combien de temps prend le KYC?', answer: 'La vérification KYC prend généralement 2-3 jours ouvrables.' },
        { question: 'Y a-t-il des frais cachés?', answer: 'Non, nous croyons en une tarification transparente sans frais cachés.' },
        { question: 'Comment réinitialiser mon mot de passe?', answer: 'Vous pouvez réinitialiser votre mot de passe via l\'app ou le site web en utilisant la vérification OTP.' }
      ],
      finalCtaTitle: 'Prêt à Découvrir la Banque Moderne?',
      finalCtaSubtitle: 'Rejoignez des millions qui font confiance à Banque SouthernWave pour leurs besoins bancaires quotidiens',
      getStarted: 'Commencer Maintenant',
      copyright: 'Tous droits réservés.',
      termsConditions: 'Termes et Conditions',
      privacyPolicy: 'Politique de Confidentialité',
      features: [
        {
          icon: '🔒',
          title: 'Banque en Ligne Sécurisée',
          description: 'Votre argent et vos données sont protégés par une sécurité et un cryptage de niveau bancaire.'
        },
        {
          icon: '🏦',
          title: 'Gérer Tous les Comptes',
          description: 'Consultez et gérez tous vos comptes en un seul endroit pratique.'
        },
        {
          icon: '⚡',
          title: 'Transferts Rapides',
          description: 'Envoyez de l\'argent instantanément avec notre système de transfert ultra-rapide.'
        },
        {
          icon: '🔔',
          title: 'Notifications Intelligentes',
          description: 'Restez informé avec des alertes et notifications intelligentes.'
        },
        {
          icon: '🕐',
          title: 'Support 24/7',
          description: 'Obtenez de l\'aide quand vous en avez besoin avec notre support client 24h/24.'
        }
      ],
      modals: {
        openAccountTitle: 'Ouvrir un Compte',
        openAccountWelcome: 'Bienvenue chez SouthernWave Bank!',
        inBranchExclusivity: 'Exclusivité en Agence',
        inBranchDesc: 'Pour garantir le plus haut niveau de sécurité et de service personnalisé, nous ouvrons actuellement de nouveaux comptes exclusivement dans nos agences.',
        visitBranch: 'Visitez Notre Agence Principale',
        hqAddressPart1: 'Centre Corporatif SouthernWave',
        hqAddressPart2: '123 Financial District, Bureau 500',
        hqAddressPart3: 'New York, NY 10004',
        hqHours: 'Horaires: Lun - Ven, 9h00 - 17h00',
        hqContact: 'Contact: 1-800-555-WAVE',
        gotItThanks: 'C\'est compris, merci!',
        welcomeBack: 'Bon Retour',
        loginSubtitle: 'Accès sécurisé à votre compte SouthernWave',
        emailLabel: 'Adresse E-mail',
        passwordLabel: 'Mot de Passe',
        rememberMe: 'Se souvenir de moi',
        forgotPasswordLink: 'Mot de Passe Oublié ?',
        loginBtn: 'Connexion',
        loggingIn: 'Connexion en cours...',
        secureEncrypted: 'Connexion sécurisée et cryptée',
        resetPasswordTitle: 'Réinitialiser le Mot de Passe',
        forgotPasswordSubtitle: 'Entrez vos coordonnées pour recevoir un OTP',
        fullNameLabel: 'Nom Complet',
        sendOtpBtn: 'Envoyer OTP',
        sending: 'Envoi...',
        backToLogin: 'Retour à la Connexion',
        enterOtpTitle: 'Entrez l\'OTP',
        enterOtpSubtitle: 'Nous avons envoyé un code par e-mail',
        otpLabel: 'OTP à 6 chiffres',
        newPasswordLabel: 'Nouveau Mot de Passe',
        verifyResetBtn: 'Vérifier et Réinitialiser',
        verifying: 'Vérification...'
      }
    },
    German: {
      aboutUs: 'Über Uns',
      contactUs: 'Kontakt',
      help: 'Hilfe',
      bankName: 'SouthernWave Bank',
      tagline: 'Erleben Sie Modernes Banking',
      login: 'Anmelden',
      openAccount: 'Konto Eröffnen',
      totalBalance: 'Gesamtsaldo',
      send: 'Senden',
      receive: 'Empfangen',
      pay: 'Bezahlen',
      securityTitle: 'Banken-Level Sicherheit',
      securitySubtitle: 'Ihr Geld und Ihre Daten sind durch branchenführende Sicherheitsmaßnahmen geschützt',
      benefits: [
        { icon: '💰', title: 'Keine Versteckten Gebühren', description: 'Transparente Preise ohne Überraschungskosten' },
        { icon: '⚡', title: '24/7 Support', description: 'Rund-um-die-Uhr Kundenbetreuung' },
        { icon: '🚀', title: 'Schnelle Einrichtung', description: 'Starten Sie in nur 5 Minuten' },
        { icon: '📊', title: 'Intelligente Einblicke', description: 'KI-gestützte Ausgabenanalyse' }
      ],
      howItWorksTitle: 'Wie Es Funktioniert',
      howItWorksSteps: [
        { step: '1', title: 'Konto Erstellen', description: 'Registrieren Sie sich mit Ihrer Handynummer' },
        { step: '2', title: 'KYC Abschließen', description: 'Verifizieren Sie Ihre Identität sicher' },
        { step: '3', title: 'Geld Hinzufügen', description: 'Füllen Sie Ihr Konto sofort auf' },
        { step: '4', title: 'Banking Starten', description: 'Genießen Sie nahtlose Banking-Erfahrung' }
      ],
      servicesTitle: 'Digitale Banking-Services',
      services: [
        { icon: '🏦', title: 'Sparkonto', description: 'Hochzins-Sparen mit Null-Saldo' },
        { icon: '💳', title: 'Debitkarten', description: 'Kontaktlose Zahlungen weltweit' },
        { icon: '🏠', title: 'Immobilienkredite', description: 'Wettbewerbsfähige Zinsen und schnelle Genehmigung' },
        { icon: '🛡️', title: 'Versicherung', description: 'Umfassende Deckungspläne' },
        { icon: '📈', title: 'Investitionen', description: 'Investmentfonds and SIP-Optionen' },
        { icon: '📱', title: 'UPI-Zahlungen', description: 'Sofortige Geldüberweisungen' }
      ],
      trustTitle: 'Vertraut von Millionen',
      trustSubtitle: 'Schließen Sie sich über 2 Millionen Kunden an, die SouthernWave Bank vertrauen',
      testimonials: [
        { name: 'Sarah Johnson', text: 'Beste Banking-App, die ich je benutzt habe. Einfach und sicher!', rating: 5 },
        { name: 'Mike Chen', text: 'Blitzschnelle Überweisungen und exzellenter Kundenservice.', rating: 5 },
        { name: 'Priya Sharma', text: 'Endlich eine Bank, die moderne Banking-Bedürfnisse versteht.', rating: 5 }
      ],
      faqTitle: 'Häufig Gestellte Fragen',
      faqs: [
        { question: 'Sind meine Daten sicher?', answer: 'Ja, wir verwenden Verschlüsselung auf Bankenniveau und erfüllen alle regulatorischen Standards.' },
        { question: 'Wie lange dauert KYC?', answer: 'KYC-Verifizierung dauert normalerweise 2-3 Werktage.' },
        { question: 'Gibt es versteckte Gebühren?', answer: 'Nein, wir glauben an transparente Preise ohne versteckte Kosten.' },
        { question: 'Wie setze ich mein Passwort zurück?', answer: 'Sie können Ihr Passwort über die App oder Website mit OTP-Verifizierung zurücksetzen.' }
      ],
      finalCtaTitle: 'Bereit für Modernes Banking?',
      finalCtaSubtitle: 'Schließen Sie sich Millionen an, die SouthernWave Bank für ihre täglichen Banking-Bedürfnisse vertrauen',
      getStarted: 'Jetzt Starten',
      copyright: 'Alle Rechte vorbehalten.',
      termsConditions: 'Geschäftsbedingungen',
      privacyPolicy: 'Datenschutzrichtlinie',
      features: [
        {
          icon: '🔒',
          title: 'Sicheres Online-Banking',
          description: 'Ihr Geld und Ihre Daten sind durch bankenübliche Sicherheit und Verschlüsselung geschützt.'
        },
        {
          icon: '🏦',
          title: 'Alle Konten Verwalten',
          description: 'Sehen und verwalten Sie alle Ihre Konten an einem praktischen Ort.'
        },
        {
          icon: '⚡',
          title: 'Schnelle Überweisungen',
          description: 'Senden Sie Geld sofort mit unserem blitzschnellen Überweisungssystem.'
        },
        {
          icon: '🔔',
          title: 'Intelligente Benachrichtigungen',
          description: 'Bleiben Sie mit intelligenten Warnungen und Benachrichtigungen informiert.'
        },
        {
          icon: '🕐',
          title: '24/7 Support',
          description: 'Erhalten Sie Hilfe, wann immer Sie sie brauchen, mit unserem rund um die Uhr verfügbaren Kundensupport.'
        }
      ],
      modals: {
        openAccountTitle: 'Konto eröffnen',
        openAccountWelcome: 'Willkommen bei SouthernWave Bank!',
        inBranchExclusivity: 'Exklusiv in der Filiale',
        inBranchDesc: 'Um ein Höchstmaß an Sicherheit und persönlichem Service zu gewährleisten, eröffnen wir neue Konten derzeit ausschließlich in unseren physischen Filialen.',
        visitBranch: 'Besuchen Sie unsere Hauptfiliale',
        hqAddressPart1: 'SouthernWave Unternehmenszentrum',
        hqAddressPart2: '123 Financial District, Suite 500',
        hqAddressPart3: 'New York, NY 10004',
        hqHours: 'Öffnungszeiten: Mo - Fr, 9:00 - 17:00 Uhr',
        hqContact: 'Kontakt: 1-800-555-WAVE',
        gotItThanks: 'Verstanden, danke!',
        welcomeBack: 'Willkommen zurück',
        loginSubtitle: 'Sicherer Zugriff auf Ihr SouthernWave-Konto',
        emailLabel: 'E-Mail-Adresse',
        passwordLabel: 'Passwort',
        rememberMe: 'Angemeldet bleiben',
        forgotPasswordLink: 'Passwort vergessen?',
        loginBtn: 'Anmelden',
        loggingIn: 'Anmelden...',
        secureEncrypted: 'Sichere verschlüsselte Verbindung',
        resetPasswordTitle: 'Passwort zurücksetzen',
        forgotPasswordSubtitle: 'Geben Sie Ihre Daten ein für das OTP',
        fullNameLabel: 'Vollständiger Name',
        sendOtpBtn: 'OTP senden',
        sending: 'Senden...',
        backToLogin: 'Zurück zur Anmeldung',
        enterOtpTitle: 'OTP eingeben',
        enterOtpSubtitle: 'Wir haben einen Code an Ihre E-Mail gesendet',
        otpLabel: '6-stellige OTP',
        newPasswordLabel: 'Neues Passwort',
        verifyResetBtn: 'Überprüfen & Zurücksetzen',
        verifying: 'Überprüfen...'
      }
    }
  };

  get currentTranslation() {
    return this.translations[this.selectedLanguage as keyof typeof this.translations];
  }

  get features() {
    return this.currentTranslation.features;
  }

  changeLanguage(language: string) {
    this.selectedLanguage = language;
  }

  getFeatureIcon(emoji: string): string {
    const iconMap: { [key: string]: string } = {
      '🔒': 'security',
      '🏦': 'account_balance',
      '⚡': 'flash_on',
      '🔔': 'notifications',
      '🕐': 'schedule'
    };
    return iconMap[emoji] || 'star';
  }

  openLogin(): void {
    this.loginModalInitialView = 'login';
    this.showLoginModal = true;
  }

  openAccountInfo(): void {
    this.showAccountModal = true;
  }

  onLogout(): void {
    this.authService.logout();
  }
}