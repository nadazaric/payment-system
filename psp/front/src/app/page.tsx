import { redirect } from "next/navigation";
import { ROUTES } from "@/const/routes";

export default function HomePage() {
    redirect(ROUTES.auth);
}
