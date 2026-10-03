# Contributing to Apartment Society Manager

Thank you for your interest in contributing to **Apartment Society Manager**! We welcome contributions to improve features, fix bugs, optimize performance, and enhance documentation.

---

## Getting Started

1. **Fork the Repository**: Click the **Fork** button on GitHub to create a personal copy of the repository.
2. **Clone the Fork**:
   ```bash
   git clone https://github.com/<your-username>/apartment-society-manager.git
   cd apartment-society-manager
   ```
3. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   # or for bug fixes:
   git checkout -b fix/issue-description
   ```

---

## Local Development & Testing

Ensure you have Java 21, Maven 3.9+, Node.js 20+, and npm installed.

### Backend

1. Navigate to `backend`:
   ```bash
   cd backend
   ```
2. Verify tests and build:
   ```bash
   ./mvnw clean test
   ```
3. Run the development server:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend

1. Navigate to `frontend`:
   ```bash
   cd frontend
   npm install
   ```
2. Verify TypeScript and production build:
   ```bash
   npm run build
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```

---

## Submission Guidelines

1. **Keep Commits Clean & Descriptive**: Use conventional commit messages (e.g., `feat: add visitor export to excel`, `fix: handle null phone number in resident form`).
2. **Never Commit Secrets**: Ensure no credentials, API keys, or `.env` files are included in your commit history.
3. **Write Unit Tests**: When introducing new business logic or fixing bugs, include appropriate unit/integration tests.
4. **Follow Code Conventions**:
   - Backend: Standard Java code formatting, Lombok annotations, Spring best practices.
   - Frontend: TypeScript strict mode, clean modular React components, Tailwind utility classes.
5. **Open a Pull Request**:
   - Push your branch to your fork: `git push origin feature/your-feature-name`
   - Open a PR against the `main` branch of this repository.
   - Provide a clear description of the problem solved and test steps conducted.
