# Third-Party Notices & Licenses

This project incorporates third-party open-source software components, libraries, and tools. Each component is subject to its respective license terms and copyright notices.

This file provides attribution and notice information for third-party software utilized in the **Apartment Society Manager** project.

---

## Backend Dependencies (Java / Maven)

| Component / Artifact | Version | License | Project URL / Source |
| :--- | :--- | :--- | :--- |
| **Spring Boot** (`spring-boot-starter-web`, `data-jpa`, `security`, `validation`, `test`) | 3.2.5 | Apache-2.0 | [spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
| **Project Lombok** (`org.projectlombok:lombok`) | 1.18.38 | MIT | [projectlombok.org](https://projectlombok.org/) |
| **PostgreSQL JDBC Driver** (`org.postgresql:postgresql`) | Latest (Managed) | BSD-2-Clause / PostgreSQL License | [jdbc.postgresql.org](https://jdbc.postgresql.org/) |
| **H2 Database Engine** (`com.h2database:h2`) | Latest (Managed) | MPL 2.0 / EPL 1.0 (Dual License) | [h2database.com](https://www.h2database.com/) |
| **Flyway Database Migration** (`flyway-core`, `flyway-database-postgresql`) | 10.10.0 | Apache-2.0 | [flywaydb.org](https://flywaydb.org/) |
| **Java JWT (JJWT)** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) | 0.12.5 | Apache-2.0 | [github.com/jwtk/jjwt](https://github.com/jwtk/jjwt) |
| **SpringDoc OpenAPI** (`springdoc-openapi-starter-webmvc-ui`) | 2.5.0 | Apache-2.0 | [springdoc.org](https://springdoc.org/) |
| **OpenPDF** (`com.github.librepdf:openpdf`) | 2.0.3 | LGPL 3.0 / MPL 2.0 (Dual License) | [github.com/LibrePDF/OpenPDF](https://github.com/LibrePDF/OpenPDF) |
| **Spring Security Test** (`spring-security-test`) | Managed | Apache-2.0 | [spring.io/projects/spring-security](https://spring.io/projects/spring-security) |

---

## Frontend Dependencies (Node.js / React)

| Package | Version | License | Source |
| :--- | :--- | :--- | :--- |
| **React & React DOM** (`react`, `react-dom`) | 18.2.0 | MIT | [react.dev](https://react.dev/) |
| **React Router DOM** (`react-router-dom`) | 6.22.3 | MIT | [reactrouter.com](https://reactrouter.com/) |
| **Axios** (`axios`) | 1.6.8 | MIT | [axios-http.com](https://axios-http.com/) |
| **Recharts** (`recharts`) | 2.12.5 | MIT | [recharts.org](https://recharts.org/) |
| **Lucide Icons** (`lucide-react`) | 0.368.0 | ISC | [lucide.dev](https://lucide.dev/) |
| **React Hook Form** (`react-hook-form`) | 7.51.3 | MIT | [react-hook-form.com](https://react-hook-form.com/) |
| **Hookform Resolvers** (`@hookform/resolvers`) | 3.3.4 | MIT | [github.com/react-hook-form/resolvers](https://github.com/react-hook-form/resolvers) |
| **Zod** (`zod`) | 3.22.4 | MIT | [zod.dev](https://zod.dev/) |
| **date-fns** (`date-fns`) | 3.6.0 | MIT | [date-fns.org](https://date-fns.org/) |
| **clsx** (`clsx`) | 2.1.0 | MIT | [github.com/lukeed/clsx](https://github.com/lukeed/clsx) |
| **tailwind-merge** (`tailwind-merge`) | 2.2.2 | MIT | [github.com/dcastil/tailwind-merge](https://github.com/dcastil/tailwind-merge) |
| **Tailwind CSS** (`tailwindcss`, `postcss`, `autoprefixer`) | 3.4.3 | MIT | [tailwindcss.com](https://tailwindcss.com/) |
| **TypeScript** (`typescript`) | 5.2.2 | Apache-2.0 | [typescriptlang.org](https://www.typescriptlang.org/) |
| **Vite** (`vite`, `@vitejs/plugin-react`) | 5.2.0 | MIT | [vitejs.dev](https://vitejs.dev/) |
| **Vitest** (`vitest`) | 1.5.0 | MIT | [vitest.dev](https://vitest.dev/) |

---

## Container & Infrastructure Images

| Image | Base / Distro | License |
| :--- | :--- | :--- |
| `postgres:16-alpine` | Alpine Linux / PostgreSQL | PostgreSQL License / BSD |
| `eclipse-temurin:21-jre-alpine` | Alpine Linux / Eclipse Temurin JDK | GPL v2 with Classpath Exception |
| `node:20-alpine` | Alpine Linux / Node.js | Node.js License (MIT-based) |
| `nginx:alpine` | Alpine Linux / Nginx | 2-clause BSD-like |

---

## License Compatibility Notice

All third-party libraries and runtime dependencies listed above are licensed under permissive or compatible open-source licenses (MIT, Apache 2.0, ISC, BSD, MPL 2.0, LGPL 3.0). The application's source code is licensed under the **MIT License** without claiming ownership over external libraries.
