# grails-spring-security-estonian-id

### Üldine kasutamine
Välja logimiseks võiks kasutada grails-spring-security plugina LogoutController-it ning vastavaid confi seadeid

Vaadetes on võimalik kasutada springsecurity taglibi. 
Näiteks:
```
<sec:ifAnyGranted roles="ROLE_USER, ROLE_ADMIN">
    <p>...</p>
</sec:ifAnyGranted>
```

Controlleris peaks kasutama ee.bitweb.grails.springsecurity.GenericSecurityService service-t, mis on SpringSecurityService põhjal loodud. 
Näiteks:
```
@Secured(['permitAll'])
class HomeController {

    GenericSecurityService genericSecurityService

    def index() {
        log.info 'current user: ' + genericSecurityService.getCurrentUser()
        log.info 'current principal ' + genericSecurityService.getPrincipal()
    }
}
```

Mobiil ID jaoks kasutaja keele tuvastamine toimub "localeResolver" bean-i kaudu.

### Seadistamine
Näite confi leiab failist DefaultEstonianIdSecurityConfig.groovy

Kõik security.estonianID nimeruumi seaded grails.plugin.springsecurity.estonianId alt.

Klassid MobileIdAuthenticationService ja IdCardAuthenticationService tegelevad vahetult autentimisega, kasutades DigiDocService-t. Ülejäänud kood on enamjaolt springsecurity vooder.

Kõik autentimisactioneid handlitakse security filtritega ja vastused tulevad JSON-is. Mobiil Id auth toimub pollides. Pollimist tuleb teha brauserist ajax-i abil. Näiteks võib vaadata test-app-i.

Id-kaart-i puhul loetakse kliendi certi sisse IdCardAuthenticationFilter::obtainCert meetodis. Teadmiseks, kui seda setup-i on vaja muuta.
