# Security Policy

## Reporting Security Issues

We take the security of **Apartment Society Manager** seriously. If you discover a security vulnerability, we appreciate your efforts to disclose it responsibly.

### Responsible Disclosure Guidelines

- **Do NOT create public GitHub issues or public pull requests for suspected security vulnerabilities.**
- Please report vulnerabilities privately to the maintainer via GitHub Private Vulnerability Reporting or via private message/email to the repository owner.
- Provide a detailed summary of the vulnerability, including:
  - Step-by-step instructions or proof-of-concept (PoC) to reproduce the vulnerability.
  - Affected components (backend endpoints, frontend components, database queries, authentication flows).
  - Potential impact and severity of the vulnerability.
  - Any suggested remediation or patch if available.
- Please give the maintainer reasonable time to investigate, remediate, and publish an update before disclosing any details publicly.

### Contact Method

To report a vulnerability:
- Please open a **Private Vulnerability Advisory** through the repository's GitHub **Security** tab (`Security > Advisories > Report a vulnerability`).
- Alternatively, contact the maintainer directly via email at [patiladitya7733@gmail.com](mailto:patiladitya7733@gmail.com) or via GitHub ([@Aditya7732](https://github.com/Aditya7732)).

### Supported Versions

| Version | Supported          |
| :------ | :----------------- |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

### Security Best Practices for Deployment

- Always change default passwords before deploying to a production or network-accessible environment.
- Never commit `.env` files or raw cryptographic keys to public or private version control.
- Ensure `JWT_SECRET` is set to a cryptographically strong, 256-bit or greater pseudo-random key.
- Enable HTTPS / TLS encryption on public entrypoints and reverse proxies.
