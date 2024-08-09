
drop table if exists companies;

CREATE TABLE companies (
	id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
	name TEXT UNIQUE NOT NULL
);

drop table if exists jobs;

create table jobs(
	id uuid primary key default gen_random_uuid (),
	companyId UUID NOT NULL,
	title text not null,
	description text not null,
	externalUrl text not null,
	salaryLo integer,
	salaryHi integer,
	currency text,
	remote boolean,
	location text not null,
	country text,

	FOREIGN KEY (companyId) REFERENCES companies(id)
);
