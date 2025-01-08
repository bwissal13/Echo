# Echo API

Une API RESTful sécurisée avec Spring Boot et JWT.

## Fonctionnalités

- 🔐 Authentification JWT avec refresh token
- 👥 Gestion des utilisateurs avec différents rôles
- 📝 Validation des données
- 📊 Monitoring avec Actuator et Prometheus
- 🚦 Rate limiting
- 💾 Cache avec Caffeine
- 📝 Documentation OpenAPI/Swagger
- 🔍 Logging avancé
- 🔄 Migration de base de données avec Liquibase

## Prérequis

- Java 17
- Maven
- PostgreSQL
- Docker (pour les tests)

## Installation

1. Cloner le repository :
```bash
git clone https://github.com/yourusername/echo01.git
cd echo01
```

2. Configurer la base de données PostgreSQL :
```bash
createdb echo_db
```

3. Configurer les variables d'environnement (ou utiliser application.properties) :
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/echo_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

4. Compiler et lancer l'application :
```bash
mvn clean install
mvn spring-boot:run
```

## Documentation API

La documentation Swagger est disponible à :
```
http://localhost:8080/swagger-ui.html
```

### Endpoints principaux

#### Authentification
- POST `/api/v1/auth/register` - Inscription
- POST `/api/v1/auth/login` - Connexion
- POST `/api/v1/auth/refresh-token` - Rafraîchir le token
- POST `/api/v1/auth/logout` - Déconnexion

## Sécurité

- Authentification JWT
- Protection CSRF désactivée (API stateless)
- Rate limiting par IP
- Validation des données
- Audit logging

## Monitoring

Endpoints Actuator disponibles :
- `/actuator/health` - État de l'application
- `/actuator/metrics` - Métriques
- `/actuator/prometheus` - Métriques pour Prometheus

## Tests

Exécuter les tests :
```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify
```

## Bonnes pratiques implémentées

1. **Architecture**
   - Clean Architecture
   - Séparation des responsabilités
   - DTOs pour la validation

2. **Sécurité**
   - Gestion sécurisée des tokens
   - Protection contre les attaques courantes
   - Validation des entrées

3. **Performance**
   - Cache
   - Rate limiting
   - Indexes de base de données

4. **Maintenance**
   - Logging complet
   - Tests automatisés
   - Documentation API

## Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## License

MIT 