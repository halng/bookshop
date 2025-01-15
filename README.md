# MyStore

> The new version of [eStore](https://github.com/tanhaok/eStore)

## Overview and component

|Component| Stand for| Language | Measure | Code Coverage |
| -- | -- | -- | -- | -- |
| `api`| api gateway| Java | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anyshop_api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anyshop_api) | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=anyshop_api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=anyshop_api) |
| `cms`| content management system -  For managing product information, categories, etc.| Java | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anyshop_cms&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anyshop_cms) | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=anyshop_cms&metric=coverage)](https://sonarcloud.io/summary/new_code?id=anyshop_cms) |
| `sar` | product search and recommend | Java | | |
| `iam`| identify and access management - JWT, OAuth for secure user sessions| Go |[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anyshop_iam&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anyshop_iam) |[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=anyshop_iam&metric=coverage)](https://sonarcloud.io/summary/new_code?id=anyshop_iam) |
| `ims`| inventory management system| Go | | |
| `oms`| order management system - Handle order processing, inventory management| Go | | |
| `pgi`| payment gateway integration - PayPal, Stripe, or other payment processors | Rust | | |
| `dms`| delivery management system | Rust | | |
| `map`| marketing and promotion | Rust | | |
| `aar`| analytics and reporting | Python | | |
| `notify`| notifications | Python | [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=anyshop_notify&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=anyshop_notify) | [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=anyshop_notify&metric=coverage)](https://sonarcloud.io/summary/new_code?id=anyshop_notify) |
| `iac` | infrastructure as code manage and generate config based on template for deployment | Python |  | | 
| `saf` | customer support and feedback | NodeJS | | |
| `media` | media: put and get image, video to/from s3 | NodeJS | | |
| `shop`| user page| Typescript | | |
| `admin`| admin page| Typescript | | |

## Architecture

To have better understanding of the architecture, please refer to the `overview.drawio`.
