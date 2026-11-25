-- =====================================================================
-- Smart Expense Tracker: database schema (Supabase / PostgreSQL)
-- Run once in Supabase: SQL Editor -> New query -> paste -> Run
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. PROFILES: one row per user, created automatically at sign up
-- ---------------------------------------------------------------------
create table public.profiles (
  id          uuid primary key references auth.users (id) on delete cascade,
  full_name   text,
  currency    text not null default 'SAR',
  language    text not null default 'en' check (language in ('en', 'ar')),
  created_at  timestamptz not null default now()
);

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles (id, full_name)
  values (
    new.id,
    coalesce(new.raw_user_meta_data ->> 'full_name',
             new.raw_user_meta_data ->> 'display_name',
             new.raw_user_meta_data ->> 'name')
  );
  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- Profiles for users who signed up before this script
insert into public.profiles (id, full_name)
select id,
       coalesce(raw_user_meta_data ->> 'full_name',
                raw_user_meta_data ->> 'display_name',
                raw_user_meta_data ->> 'name')
from auth.users
on conflict (id) do nothing;

-- ---------------------------------------------------------------------
-- 2. CATEGORIES: default ones (user_id is null) + each user's custom ones
-- ---------------------------------------------------------------------
create table public.categories (
  id          uuid primary key default gen_random_uuid(),
  user_id     uuid references auth.users (id) on delete cascade,
  name        text not null,
  icon        text,
  color       text,
  created_at  timestamptz not null default now()
);

insert into public.categories (name, icon, color) values
  ('Food',           'restaurant',     '#E8A33D'),
  ('Transportation', 'directions_car', '#3D7BE8'),
  ('Shopping',       'shopping_bag',   '#B05BE0'),
  ('Bills',          'receipt',        '#E05B5B'),
  ('Entertainment',  'movie',          '#2BB3A3'),
  ('Other',          'category',       '#7A8783');

-- ---------------------------------------------------------------------
-- 3. EXPENSES
-- ---------------------------------------------------------------------
create table public.expenses (
  id           uuid primary key default gen_random_uuid(),
  user_id      uuid not null default auth.uid() references auth.users (id) on delete cascade,
  category_id  uuid references public.categories (id) on delete set null,
  amount       numeric(12, 2) not null check (amount > 0),
  description  text,
  merchant     text,
  spent_on     date not null default current_date,
  source       text not null default 'manual'
               check (source in ('manual', 'sms', 'csv', 'recurring')),
  created_at   timestamptz not null default now()
);
create index expenses_user_date_idx on public.expenses (user_id, spent_on);

-- ---------------------------------------------------------------------
-- 4. BUDGETS: monthly total (category_id is null) or per category
-- ---------------------------------------------------------------------
create table public.budgets (
  id              uuid primary key default gen_random_uuid(),
  user_id         uuid not null default auth.uid() references auth.users (id) on delete cascade,
  category_id     uuid references public.categories (id) on delete cascade,
  month           date not null,              -- first day of the month, e.g. 2026-11-01
  amount          numeric(12, 2) not null check (amount > 0),
  alert_percents  int[] not null default '{80,100}',
  created_at      timestamptz not null default now(),
  unique nulls not distinct (user_id, category_id, month)
);

-- ---------------------------------------------------------------------
-- 5. RECURRING BILLS (rent, subscriptions...)
-- ---------------------------------------------------------------------
create table public.recurring_bills (
  id                  uuid primary key default gen_random_uuid(),
  user_id             uuid not null default auth.uid() references auth.users (id) on delete cascade,
  category_id         uuid references public.categories (id) on delete set null,
  name                text not null,
  amount              numeric(12, 2) not null check (amount > 0),
  frequency           text not null check (frequency in ('weekly', 'monthly', 'yearly')),
  next_due_date       date not null,
  remind_days_before  int not null default 2 check (remind_days_before >= 0),
  is_active           boolean not null default true,
  created_at          timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- 6. SAVINGS GOALS + CONTRIBUTIONS
-- ---------------------------------------------------------------------
create table public.savings_goals (
  id             uuid primary key default gen_random_uuid(),
  user_id        uuid not null default auth.uid() references auth.users (id) on delete cascade,
  name           text not null,
  target_amount  numeric(12, 2) not null check (target_amount > 0),
  deadline       date,
  created_at     timestamptz not null default now()
);

create table public.savings_contributions (
  id              uuid primary key default gen_random_uuid(),
  user_id         uuid not null default auth.uid() references auth.users (id) on delete cascade,
  goal_id         uuid not null references public.savings_goals (id) on delete cascade,
  amount          numeric(12, 2) not null check (amount > 0),
  contributed_on  date not null default current_date,
  created_at      timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- 7. REMINDER SETTINGS: 1 to 5 reminder times per day
-- ---------------------------------------------------------------------
create table public.reminder_settings (
  user_id     uuid primary key default auth.uid() references auth.users (id) on delete cascade,
  enabled     boolean not null default true,
  times       time[] not null default '{09:00,13:00,18:00,22:00}',
  updated_at  timestamptz not null default now(),
  check (cardinality(times) between 1 and 5)
);

-- ---------------------------------------------------------------------
-- 8. MERCHANT CATEGORY MEMORY (learns from the user's corrections)
-- ---------------------------------------------------------------------
create table public.merchant_categories (
  user_id      uuid not null default auth.uid() references auth.users (id) on delete cascade,
  merchant     text not null,
  category_id  uuid not null references public.categories (id) on delete cascade,
  updated_at   timestamptz not null default now(),
  primary key (user_id, merchant)
);

-- ---------------------------------------------------------------------
-- 9. CONSENT RECORDS (privacy: SMS capture, AI features)
-- ---------------------------------------------------------------------
create table public.consent_records (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null default auth.uid() references auth.users (id) on delete cascade,
  consent_type  text not null
                check (consent_type in ('privacy_notice', 'sms_capture', 'ai_features')),
  granted       boolean not null,
  created_at    timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- 10. AI REPORTS AND SUGGESTIONS
-- ---------------------------------------------------------------------
create table public.ai_reports (
  id            uuid primary key default gen_random_uuid(),
  user_id       uuid not null default auth.uid() references auth.users (id) on delete cascade,
  period_start  date not null,
  period_end    date not null,
  summary       text,
  suggestions   jsonb,
  created_at    timestamptz not null default now(),
  check (period_end >= period_start)
);

-- =====================================================================
-- ROW LEVEL SECURITY: each user can only see and change their own data
-- =====================================================================
alter table public.profiles              enable row level security;
alter table public.categories            enable row level security;
alter table public.expenses              enable row level security;
alter table public.budgets               enable row level security;
alter table public.recurring_bills       enable row level security;
alter table public.savings_goals         enable row level security;
alter table public.savings_contributions enable row level security;
alter table public.reminder_settings     enable row level security;
alter table public.merchant_categories   enable row level security;
alter table public.consent_records       enable row level security;
alter table public.ai_reports            enable row level security;

create policy "Own profile" on public.profiles
  for all to authenticated
  using ((select auth.uid()) = id) with check ((select auth.uid()) = id);

create policy "See default and own categories" on public.categories
  for select to authenticated
  using (user_id is null or (select auth.uid()) = user_id);
create policy "Add own categories" on public.categories
  for insert to authenticated with check ((select auth.uid()) = user_id);
create policy "Edit own categories" on public.categories
  for update to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Delete own categories" on public.categories
  for delete to authenticated using ((select auth.uid()) = user_id);

create policy "Own expenses" on public.expenses
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own budgets" on public.budgets
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own recurring bills" on public.recurring_bills
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own savings goals" on public.savings_goals
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own savings contributions" on public.savings_contributions
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own reminder settings" on public.reminder_settings
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own merchant categories" on public.merchant_categories
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own consent records" on public.consent_records
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "Own AI reports" on public.ai_reports
  for all to authenticated
  using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);

-- Let signed-in users reach these tables through the app (RLS still applies)
grant select, insert, update, delete on all tables in schema public to authenticated;
